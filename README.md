# groovydxf

A Groovy library to write AutoCad files (DXF R12 version only)

## Sample code

```
def drawing = new Drawing()
def modelSpace = drawing.modelSpace

// writing a line
modelSpace.addLine([0, 0], [2, 2])

// writing a text
modelSpace.addText("simple text").setPos([2, 3])

// writing a circle
modelSpace.addCircle([1.3f, 4.4f], 5, ['color': 7])

// writing an arc
block.addArc([-0.0907f, 1.4963f], 2.0150f, 359f, 48f)

// writing a 2d polyline
block.addPolyline2d([[-1.25f, -1.25f],
                     [1.25f, -1.25f],
                     [1.25f, 1.25f],
                     [-1.25f, 1.25f]])

// block creation
DXF12BlockLayout block = drawing.blocks.newBlock('TEST')
block.addLine([-1, -1], [1, 1])
block.addLine([-1, 1], [1, -1])

// block usage
modelSpace.addBlockRef('TEST', [5, 5], ['rotation': 45f])

drawing.save('sample.dxf')
```