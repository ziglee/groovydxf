package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.DefaultChunk
import net.cassiolandim.dxf.lldxf.Tagger
import net.cassiolandim.dxf.lldxf.Tags

class Sections {

    private static KNOWN_SECTIONS = ['header', 'classes', 'tables', 'blocks', 'entities', 'objects', 'thumbnailimage', 'acdsdata']

    def sections = [:]

    public Sections(List<DXFTag> tagReader, Drawing drawing) {
        def bootstrap = true
        def comments = []

        for (Tags section in DefaultChunk.iterChunks(Tagger.skipComments(tagReader, comments).iterator())) {
            if (bootstrap) {
                def newSection = new HeaderSection(section)
                section = null // this tags are done
                drawing.bootstrapHook(newSection, comments)
                newSection.headerVarFactory = drawing.dxfFactory.headerVarFactory
                bootstrap = false
                sections.put newSection.name, newSection
            }

            if (section != null) {
                def newSection
                switch (section[1].value) {
                    case 'CLASSES':
                        newSection = new ClassesSection(section, drawing)
                        break
                    case 'TABLES':
                        newSection = new TablesSection(section, drawing)
                        break
                    case 'BLOCKS':
                        newSection = new BlocksSection(section, drawing)
                        break
                    case 'ENTITIES':
                        newSection = new EntitySection(section, drawing)
                        break
                    case 'OBJECTS':
                        newSection = new ObjectsSection(section, drawing)
                        break
                }
                sections.put newSection.name, newSection
            }
        }

        createRequiredSections(drawing)
    }

    def createRequiredSections(Drawing drawing) {
        if (!sections.containsKey('blocks'))
            sections.put 'blocks', new BlocksSection(null, drawing)
        if (!sections.containsKey('tables'))
            sections.put 'tables', new TablesSection(null, drawing)
    }

    void write(File stream) {
        def writeOrder = (List) KNOWN_SECTIONS.clone()
        def unknownSections = sections.keySet().minus(KNOWN_SECTIONS)
        if (!unknownSections.isEmpty()) {
            writeOrder.addAll(unknownSections)
        }

        def writtenSections = []
        for (String sec in KNOWN_SECTIONS) {
            def section = sections.get(sec)
            if (section) {
                section.write(stream)
                writtenSections.add(section.name)
            }
        }

        stream.append('  0\nEOF\n') //System.getProperty("line.separator")
    }

    @Override
    Object getProperty(String property) {
        def result = sections.get(property)
        if (!result) throw new NoSuchElementException(property)
        return result
    }
}
