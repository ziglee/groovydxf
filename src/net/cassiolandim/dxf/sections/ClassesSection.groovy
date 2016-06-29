package net.cassiolandim.dxf.sections

import net.cassiolandim.dxf.Drawing
import net.cassiolandim.dxf.EntitySpace
import net.cassiolandim.dxf.lldxf.DXFTag
import net.cassiolandim.dxf.lldxf.Tags

class ClassesSection extends AbstractSection {

    ClassesSection(Tags tags, Drawing drawing) {
        super(new EntitySpace(drawing.entitydb), tags, drawing)
    }

    @Override
    String getName() {
        return 'classes'
    }

// TODO
//      def __iter__(self):  // no layout setting required/possible
//    for handle in self._entity_space:
//    yield self.dxffactory.wrap_handle(handle)
}
