package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.legacy.layouts.DXF12BlockLayout
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.TagGroups
import net.cassiolandim.dxf.lldxf.Tagger
import net.cassiolandim.dxf.lldxf.Tags

class BlocksSection {

    Drawing drawing
    def blockLayouts = new LinkedHashMap<String, ClassifiedTags>()

    /**
     * Mapping of BlockLayouts, key is BlockLayout.name, for dict() order of blocks is random,
     * if turns out later, that blocks order is important: use an OrderedDict().
     */
    BlocksSection(Tags tags, Drawing drawing) {
        this.drawing = drawing
        if (tags)
            build(tags)
    }

    def build(Tags tags) {
        Closure<String> addTags = { ClassifiedTags ctags ->
            return drawing.entitydb.addTags(ctags)
        }

        Closure<DXF12BlockLayout> buildBlockLayout = { List<ClassifiedTags> entities ->
            String tailHandle = addTags(entities.pop())
            def entitiesIterator = entities.iterator()
            String headHandle = addTags(entitiesIterator.next())
            def block = drawing.dxfFactory.newBlockLayout(headHandle, tailHandle)

            while (entitiesIterator.hasNext()) {
                ClassifiedTags entity = entitiesIterator.next()
                String handle = drawing.entitydb.addTags(entity)
                if (!ClassifiedTags.getTagsLinker(entity, handle))  // also creates the link structure as side effect
                    block.addHandle(handle)
            }

            return block
        }

        if (!tags[0].equals(new DXFTag(0, 'SECTION')) || !tags[1].equals(new DXFTag(2, 'BLOCKS')) || !tags[-1].equals(new DXFTag(0, 'ENDSEC'))) {
            throw new Tagger.DXFStructureError("Critical structure error in HEADER section.")
        }

        if (tags.size() == 3)  // DXF file with empty header section
            return

        def entities = new ArrayList<ClassifiedTags>()
        def groups = new TagGroups(tags.subList(2, tags.size() - 1))
        for (List<DXFTag> group in groups) {
            def ctags = new ClassifiedTags(group)
            entities.add(ctags)
            if (group[0].value.equals('ENDBLK')) {
                add(buildBlockLayout(entities))
                entities = new ArrayList<ClassifiedTags>()
            }
        }
    }

    String getName() {
        return 'blocks'
    }

    void write(File stream) {
        stream.append("  0\nSECTION\n  2\nBLOCKS\n")
        for (block in blockLayouts.values()) {
            block.write(stream)
        }
        stream.append("  0\nENDSEC\n")
    }

    /**
     * Add or replace a BlockLayout() object.
     */
    def add(DXF12BlockLayout blockLayout) {
        blockLayouts.put(blockLayout.name, blockLayout)
    }

    /**
     * Create a new named block.
     */
    DXF12BlockLayout newBlock(String name, def basePoint = [0, 0], Map dxfAttribs = [:]) {
        dxfAttribs.put('name', name)
        dxfAttribs.put('name2', name)
        dxfAttribs.put('base_point', basePoint)

        def head = drawing.dxfFactory.createDbEntry('BLOCK', dxfAttribs)
        def tail = drawing.dxfFactory.createDbEntry('ENDBLK', [:])
        def blockLayout = drawing.dxfFactory.newBlockLayout(head.dxf.handle, tail.dxf.handle)
        add(blockLayout)
        return blockLayout
    }
}
