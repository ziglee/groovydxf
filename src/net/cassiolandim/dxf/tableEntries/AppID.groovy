package net.cassiolandim.dxf.tableEntries

import net.cassiolandim.dxf.DXFEntity
import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttributes

class AppID extends DXFEntity {

    // TODO
    AppID(ClassifiedTags tags, Drawing drawing) {
        super(tags, drawing)
    }

    // TODO
    @Override
    ClassifiedTags getTemplate() {
        return null
    }

    // TODO
    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return null
    }
}
