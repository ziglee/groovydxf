package net.cassiolandim.dxf.graphics

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.lldxf.ClassifiedTags
import net.cassiolandim.dxf.lldxf.DXFAttr
import net.cassiolandim.dxf.lldxf.DXFAttributes

class Vertex extends GraphicEntity {

    private static final int VTX_EXTRA_VERTEX_CREATED = 1 // Extra vertex created by curve-fitting
    private static final int VTX_CURVE_FIT_TANGENT = 2 // Curve-fit tangent defined for this vertex.
    // A curve-fit tangent direction of 0 may be omitted from the DXF output, but is
    // significant if this bit is set.
    // 4 = unused, never set in dxf files
    private static final int VTX_SPLINE_VERTEX_CREATED = 8 // Spline vertex created by spline-fitting
    private static final int VTX_SPLINE_FRAME_CONTROL_POINT = 16
    private static final int VTX_3D_POLYLINE_VERTEX = 32
    private static final int VTX_3D_POLYGON_MESH_VERTEX = 64
    private static final int VTX_3D_POLYFACE_MESH_VERTEX = 128

    public static final def VERTEX_FLAGS = [
            'AcDb2dPolyline': 0,
            'AcDb3dPolyline': VTX_3D_POLYLINE_VERTEX,
            'AcDbPolygonMesh': VTX_3D_POLYGON_MESH_VERTEX,
            'AcDbPolyFaceMesh': VTX_3D_POLYGON_MESH_VERTEX | VTX_3D_POLYFACE_MESH_VERTEX,
    ]

    private static final String VERTEX_TPL = """ 0
VERTEX
  5
0
  8
0
 10
0.0
 20
0.0
 30
0.0
 40
0.0
 41
0.0
 42
0.0
 70
0
"""

    Vertex(ClassifiedTags tags, Drawing drawing = null) {
        super(tags, drawing)
    }

    Vertex(String handle, Map dxfAttribs, Drawing drawing) {
        super(handle, dxfAttribs, drawing)
    }

    @Override
    ClassifiedTags getTemplate() {
        return ClassifiedTags.fromText(VERTEX_TPL)
    }

    @Override
    DXFAttributes getDefaulDxfAttribs() {
        return makeAttribs([
                'location': new DXFAttr(10, 'Point2D/3D'),
                'start_width': new DXFAttr(40, null, 0.0),
                'end_width': new DXFAttr(41, null, 0.0),
                'bulge': new DXFAttr(42, null, 0),
                'flags': new DXFAttr(70, null, 0),
                'tangent': new DXFAttr(50),
                'vtx0': new DXFAttr(71),
                'vtx1': new DXFAttr(72),
                'vtx2': new DXFAttr(73),
                'vtx3': new DXFAttr(74)
        ])
    }

    // TODO QuadrilateralMixin
}
