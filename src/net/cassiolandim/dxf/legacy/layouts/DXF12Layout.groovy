package net.cassiolandim.dxf.legacy.layouts

import net.cassiolandim.dxf.DXFEntity
import net.cassiolandim.dxf.EntitySpace
import net.cassiolandim.dxf.legacy.LegacyDXFFactory

/**
 * Layout representation
 */
class DXF12Layout extends BaseLayout {

    int paperSpace = 0

    DXF12Layout(EntitySpace entitySpace, LegacyDXFFactory factory, int paperSpace = 0) {
        super(factory, entitySpace)
        this.paperSpace = paperSpace
    }

    void setPaperSpace(DXFEntity entity) {
        entity.dxf.paperspace = paperSpace
    }

    int getLayoutKey() {
        return paperSpace
    }
}
