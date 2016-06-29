package net.cassiolandim.dxf

import net.cassiolandim.dxf.graphics.Arc
import net.cassiolandim.dxf.graphics.Circle
import net.cassiolandim.dxf.graphics.Insert
import net.cassiolandim.dxf.graphics.Line
import net.cassiolandim.dxf.graphics.Point
import net.cassiolandim.dxf.graphics.Polyline
import net.cassiolandim.dxf.graphics.Text
import net.cassiolandim.dxf.legacy.LegacyDXFFactory

/**
 * Abstract base class for BaseLayout()
 */
abstract class GraphicsFactory {

    LegacyDXFFactory dxfFactory

    GraphicsFactory(LegacyDXFFactory dxfFactory) {
        this.dxfFactory = dxfFactory
    }

    abstract def buildAndAddEntity(String type, Map dxfAttribs);

    Point addPoint(def location, Map dxfAttribs = [:]) {
        dxfAttribs.put('location', location)
        return buildAndAddEntity('POINT', dxfAttribs)
    }

    Line addLine(def start, def end, Map dxfAttribs = [:]) {
        dxfAttribs.put 'start', start
        dxfAttribs.put 'end', end
        return buildAndAddEntity('LINE', dxfAttribs)
    }

    Circle addCircle(def center, def radius, Map dxfAttribs = [:]) {
        dxfAttribs.put('center', center)
        dxfAttribs.put 'radius', radius
        return buildAndAddEntity('CIRCLE', dxfAttribs)
    }

    Polyline addPolyline2d(def points, Map dxfAttribs = [:]) {
        boolean closed = dxfAttribs.get('closed', false)
        dxfAttribs.remove('closed')
        Polyline entity = buildAndAddEntity('POLYLINE', dxfAttribs)
        entity.close(closed)
        entity.appendVertices(points)
        return entity
    }

    Arc addArc(def center, float radius, float startAngle, float endAngle, Map dxfAttribs = [:]) {
        dxfAttribs.put('center', center)
        dxfAttribs.put('radius', radius)
        dxfAttribs.put('start_angle', startAngle)
        dxfAttribs.put('end_angle', endAngle)
        return buildAndAddEntity('ARC', dxfAttribs)
    }

    Text addText(String text, Map dxfAttribs = [:]) {
        dxfAttribs.put('text', StringUtil.toHexString(text))
        if (!dxfAttribs.containsKey('insert'))
            dxfAttribs.put('insert', [0, 0])
        return buildAndAddEntity('TEXT', dxfAttribs)
    }

    Insert addBlockRef(String name, def insert, Map dxfAttribs = [:]) {
        dxfAttribs.put('name', name)
        dxfAttribs.put('insert', insert)
        return buildAndAddEntity('INSERT', dxfAttribs)
    }
}
