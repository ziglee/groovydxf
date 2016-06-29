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

Copyright © 2000 Cassio Landim
This work is free. You can redistribute it and/or modify it under the
terms of the Do What The Fuck You Want To Public License, Version 2,
as published by Sam Hocevar. See the COPYING file for more details.

![Alt text](http://www.wtfpl.net/wp-content/uploads/2012/12/wtfpl-badge-1.png "WTFPL")