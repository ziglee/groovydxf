package net.cassiolandim.dxf.lldxf

import net.cassiolandim.dxf.Drawing

class DefaultChunk {

    Tags tags
    Drawing drawing

    DefaultChunk(Tags tags, Drawing drawing) {
        this.tags = tags
        this.drawing = drawing
    }

    String getName() {
        return String.valueOf(tags[1].value).toLowerCase()
    }

    public static List<Tags> iterChunks(Iterator<DXFTag> iterator, String stopTag = 'EOF', String endOfChunk = 'ENDSEC') {
        def chunks = new ArrayList<DXFTag>()

        while (iterator.hasNext()) {
            def tag = iterator.next()
            if (tag.value.equals(stopTag)) {
                return chunks
            }
            def chunk = new Tags([tag])
            while (!tag.value.equals(endOfChunk) && iterator.hasNext()) {
                tag = iterator.next()
                chunk << tag
            }
            chunks << chunk
        }

        return chunks
    }
}
