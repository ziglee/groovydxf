package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.DXFEntity
import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

/**
 * Default graphic entity wrapper, allows access to following dxf attributes:
 - handle
 - layer
 - linetype
 - color
 - paperspace
 - extrusion
 Wrapper for all unsupported graphic entities.
 */
class GraphicEntity extends DXFEntity {

    GraphicEntity(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    GraphicEntity(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return null
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs()
    }

    static DXFAttributes makeAttribs(Map<String, DXFAttr> additional = null) {
        def dxfAttribs = [
            'handle': new DXFAttr(5),
            'layer': new DXFAttr(8, null, '0'), // layername as string
            'linetype': new DXFAttr(6, null, 'BYLAYER'), // linetype as string, special names BYLAYER/BYBLOCK
            'color': new DXFAttr(62, null, 256), // dxf color index, 0 .. BYBLOCK, 256 .. BYLAYER
            'thickness': new DXFAttr(39, null, 0), // thickness of 2D elements
            'paperspace': new DXFAttr(67, null, 0), // 0 .. modelspace, 1 .. paperspace
            'extrusion': new DXFAttr(210, 'Point3D', [0.0, 0.0, 1.0]), // Z-axis of OCS (Object-Coordinate-System)
        ]
        if (additional) dxfAttribs.putAll(additional)
        return new DXFAttributes(dxfAttribs)
    }
}
