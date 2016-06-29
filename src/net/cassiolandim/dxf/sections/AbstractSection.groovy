package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.LayoutSpaces
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.TagGroups
import net.cassiolandim.dxf.lldxf.Tagger
import net.cassiolandim.dxf.lldxf.Tags

abstract class AbstractSection {

    Drawing drawing
    def entitySpace // EntitySpace or LayoutSpaces

    AbstractSection(def entitySpace, Tags tags, Drawing drawing) {
        this.entitySpace = entitySpace
        this.drawing = drawing
        if (tags)
            build(tags)
    }

    private void build(Tags tags) {
        if (!tags[0].equals(new DXFTag(0, 'SECTION')) ||
                !tags[1].equals(new DXFTag(2, name.toUpperCase())) ||
                !tags[-1].equals(new DXFTag(0, 'ENDSEC'))) {
            throw new Tagger.DXFStructureError("Critical structure error in ${name.toUpperCase()} section.")
        }

        if (tags.size() == 3)  // DXF file with empty header section
            return

        def groups = new TagGroups(tags.subList(2, tags.size() - 1))
        for (group in groups) {
            def ctags = new ClassifiedTags(group)
            def handler = drawing.entitydb.addTags(ctags)
            if (!ClassifiedTags.getTagsLinker(ctags, handler)) { // also creates the link structure as side effect
                entitySpace.storeTags(ctags) // add to entity space
            }
        }
    }

    abstract String getName()
}
