package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.LayoutSpaces
import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.Tags

class EntitySection extends AbstractSection {

    EntitySection(Tags tags, Drawing drawing) {
        super(new LayoutSpaces(drawing.entitydb), tags, drawing)
    }

    def getLayoutSpace(int index) {
        return entitySpace.getEntitySpace(index)
    }

    @Override
    String getName() {
        return 'entities'
    }

    void write(File stream) {
        stream.append("  0\nSECTION\n  2\n${name.toUpperCase()}\n")
        // Just write *Model_Space and the active *Paper_Space into the ENTITIES section.
        def layoutKeys = drawing.getActiveEntitySpaceLayoutKeys()
        entitySpace.write(stream, layoutKeys)
        stream.append("  0\nENDSEC\n")
    }
}
