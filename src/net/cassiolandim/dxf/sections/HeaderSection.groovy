package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.StringUtil
import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.TagGroups
import net.cassiolandim.dxf.lldxf.Tagger

class HeaderSection {

    List<DXFTag> tags
    def headerVarFactory
    def hdrvars = new LinkedHashMap<String, HeaderVar>()
    def customVars = new CustomVars()

    public HeaderSection(List<DXFTag> tags) {
        this.tags = tags
        build(tags)
    }

    String getName() {
        return 'header'
    }

    String get(String key, String defaultCase = null) {
        if (hdrvars.containsKey(key))
            return hdrvars.get(key).value
        else
            return defaultCase
    }

    void set(String key, def value) {
        def tags = headerVarFactory(key, value)
        hdrvars.put key, new HeaderVar(tags)
    }

    void write(File stream) {
        def _write = { String name, def value ->
            stream.append("  9\n${name}\n")
            stream.append(StringUtil.ustr(value))
        }

        stream.append("  0\nSECTION\n  2\nHEADER\n")

        hdrvars.each { String key, HeaderVar headerVar ->
            _write(key, headerVar)
            if (key.equals("\$LASTSAVEDBY")) { // ugly hack, but necessary for AutoCAD
                customVars.write(stream)
            }
        }

        stream.append("  0\nENDSEC\n")
    }

    private void build(List<DXFTag> tags) {
        if (!tags[0].equals(new DXFTag(0, 'SECTION')) ||
                !tags[1].equals(new DXFTag(2, 'HEADER')) ||
                !tags[-1].equals(new DXFTag(0, 'ENDSEC'))) {
            throw new Tagger.DXFStructureError("Critical structure error in HEADER section.")
        }

        if (tags.size() == 3)  // DXF file with empty header section
            return

        def groups = new TagGroups(tags.subList(2, tags.size() - 1), 9)
        def customPropertyStack = [] // collect $CUSTOMPROPERTY/TAG
        for (group in groups) {
            def name = group[0].value
            def value = group[1]
            if (name.equals('$CUSTOMPROPERTYTAG') || name.equals('$CUSTOMPROPERTY'))
                customPropertyStack.add value.value
            else
                hdrvars.put name, new HeaderVar(value)
        }

        customPropertyStack = customPropertyStack.reverse()
        if (customPropertyStack) {
            (0..<customPropertyStack.size()).each {
                customVars.append(customPropertyStack.pop(), customPropertyStack.pop())
            }
        }
    }

    /**
     * Custom Properties are stored as string tuples ('CustomTag', 'CustomValue') in a list object.
     * Multiple occurrence of the same 'CustomTag' is allowed, but not well supported by the interface.
     */
    public static class CustomVars {

        def properties = []

        void append(def tag, def value) { // custom properties always stored as strings
            properties.add([tag, StringUtil.ustr(value)])
        }

        void write(File stream) {
            properties.each {
                stream.append("  9\n\$CUSTOMPROPERTYTAG\n  1\n${it[0]}\n")
                stream.append("  9\n\$CUSTOMPROPERTY\n  1\n${it[1]}\n")
            }
        }
    }
}
