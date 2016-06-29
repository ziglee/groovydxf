package net.cassiolandim.dxf.lldxf

class ClassifiedTags {

    private static final def LINKED_ENTITIES = [
        'INSERT': 'ATTRIB',
        'POLYLINE': 'VERTEX'
    ]

    List<Tags> appData = new ArrayList<Tags>() // code == 102, keys are "{<arbitrary name>", values are Tags()
    List<Tags> subClasses = new ArrayList<Tags>() // code == 100, keys are "subclassname", values are Tags()
    List<Tags> xData = new ArrayList<Tags>() // code >= 1000, keys are "APPNAME", values are Tags()
    def link // link to following entities like INSERT -> ATTRIB and POLYLINE -> VERTEX

    private static final int APP_DATA_MARKER = 102
    private static final int SUBCLASS_MARKER = 100
    private static final int XDATA_MARKER = 1001

    ClassifiedTags(def arg = null) {
        if (arg && arg instanceof String)
            throw new IllegalArgumentException('use ClassifiedTags.from_text() to create tags from a string.')

        if (arg)
            setup(arg)
    }

    void setup(List<DXFTag> iterable) {
        final def tagStream = iterable.iterator()

        Closure<Boolean> isAppData = { DXFTag tag ->
            return tag.code == APP_DATA_MARKER && tag.value.startswith('{')
        }

        def collectAppData = { DXFTag startTag ->
            //appdata, cannot contain xdata or subclasses
            def data = new Tags([startTag])
            while (tagStream.hasNext()) {
                def tag = tagStream.next()
                data.add(tag)
                if (tag.code == APP_DATA_MARKER)
                    break
            }
            appData.add(data)
        }

        Closure<DXFTag> collectSubclass = { DXFTag startTag ->
            // A subclass can contain appdata, but not xdata, ends with SUBCLASSMARKER or XDATACODE.
            def data = startTag ? new Tags([startTag]) : new Tags()
            while (tagStream.hasNext()) {
                def tag = tagStream.next()
                if (isAppData(tag)) {
                    def appDataPos = appData.size()
                    data.add(new DXFTag(tag.code, appDataPos))
                    collectAppData(tag)
                } else if (tag.code == SUBCLASS_MARKER || tag.code == XDATA_MARKER) {
                    subClasses.add(data)
                    return tag
                } else {
                    data.add(tag)
                }
            }
            subClasses.add(data)
            return null
        }

        Closure<DXFTag> collectXdata = { DXFTag startTag ->
            // xdata are always at the end of the entity and can not contain appdata or subclasses
            def data = new Tags([startTag])
            while (tagStream.hasNext()) {
                def tag = tagStream.next()
                if (tag.code == XDATA_MARKER) {
                    xData.add(data)
                    return tag
                } else {
                    data.add(tag)
                }
            }
            xData.add(data)
            return null
        }

        DXFTag tag = collectSubclass(null) // preceding tags without a subclass
        while (tag && tag.code == SUBCLASS_MARKER)
            tag = collectSubclass(tag)
        while (tag && tag.code == XDATA_MARKER)
            tag = collectXdata(tag)

        if (tag) throw new Tagger.DXFStructureError("Unexpected tag '${tag}' at end of entity.")
    }

    String getHandle() {
        return subClasses[0].getHandle()
    }

    def dxfType() {
        return noClass[0].value
    }

    Tags getNoClass() {
        return subClasses[0]
    }

    Tags getSubClass(String name, int pos = 0) {
        int getPos = 0
        for (subClass in subClasses) {
            if (subClass.size() && subClass[0].value.equals(name) && getPos >= pos) {
                return subClass
            }
            getPos += 1
        }
        throw new NoSuchElementException("Subclass '${name}' does not exist.")
    }

    public static boolean getTagsLinker(ClassifiedTags tags, String handle) {
        // Parameter handle is necessary, because DXF12 did not require a handle, therefor the
        // handle isn't stored in tags and tags.get_handle() fails with an ValueError

        def attribsFollow = {
            def refTags
            try {
                refTags = tags.getSubClass('AcDbBlockReference')
            } catch (e) {
                return false
            }
            return Boolean.parseBoolean(refTags.findFirst(66, 0))
        }

        boolean areLinkedTags = false // INSERT & POLYLINE are not linked tags, they are stored in the entity space
        def dxfType = tags.dxfType()
        def vars = new PersistentVars()

        if (vars.prev) {
            areLinkedTags = true
            // VERTEX, ATTRIB & SEQEND are linked tags, they are NOT stored in the entity space
            if (dxfType.equals('SEQEND')) {
                vars.prev.link = handle
                vars.prev = null
            } else if (dxfType.equals(vars.expected)) { // check for valid DXF structure just VERTEX follows POLYLINE and just ATTRIB follows INSERT
                vars.prev.link = handle
                vars.prev = tags
            } else {
                throw new Tagger.DXFStructureError("expected DXF entity ${dxfType} or SEQEND")
            }
        } else if (dxfType.equals('INSERT') || dxfType.equals('POLYLINE')) { // only these two DXF types have this special linked structure
            if (dxfType.equals('INSERT') && !attribsFollow()) {
                /*
                 * INSERT must not have following ATTRIBS, ATTRIB can be a stand alone entity:
                 * INSERT with no ATTRIBS, attribs_follow == 0
                 * ATTRIB as stand alone entity
                 * ....
                 * INSERT with ATTRIBS, attribs_follow == 1
                 * ATTRIB as connected entity
                 * SEQEND
                 * Therefor a ATTRIB following an INSERT doesn't mean that these entities are connected.
                 */
            } else {
                vars.prev = tags
                vars.expected = LINKED_ENTITIES.get(dxfType)
            }
        }

        return areLinkedTags // caller should know, if *tags* should be stored in the entity space or not
    }

    private static class PersistentVars {
        def prev
        String expected = ''
    }

    static ClassifiedTags fromText(String text) {
        return new ClassifiedTags(Tagger.skipComments(Tagger.stringTagger(text)))
    }

    void write(File stream) {
        Tags.writeTags(stream, iter())
    }

    List<DXFTag> iter() {
        def list = new ArrayList<DXFTag>()
        for (Tags subClass in subClasses) {
            for (tag in subClass) {
                if (tag.code == APP_DATA_MARKER && (tag.value instanceof int || tag.value instanceof Integer)) {
                    for (subTag in appData[tag.value]) {
                        list << subTag
                    }
                } else {
                    list << tag
                }
            }
        }
        for (x in xData) {
            for (tag in x) {
                list << tag
            }
        }
        return list
    }
}
