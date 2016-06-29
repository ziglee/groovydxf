package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.Tags

class ObjectsSection extends AbstractSection {

    ObjectsSection(Tags tags, Drawing drawing) {
        super(null, tags, drawing)
    }

    @Override
    String getName() {
        return 'objects'
    }
}
