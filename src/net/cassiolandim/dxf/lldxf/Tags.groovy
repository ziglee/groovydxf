package net.cassiolandim.dxf.lldxf

/**
 * DXFTag() chunk as flat list.
 */
class Tags extends ArrayList<DXFTag> {

    Tags(){}

    Tags(List<DXFTag> tags) {
        super(tags)
    }

    static void writeTags(File stream, List<DXFTag> tags) {
        for (tag in tags) {
            //if isinstance(tag, CompressedTags):
                //tag.write(stream)
            //else:
            stream.append(Types.strTag2(tag))
        }
    }

    /**
     * Get DXF handle. Raises ValueError if handle not exists.
     * @return handle as hex-string like 'FF'
     */
    String getHandle() {
        def handle = ''
        for (DXFTag tag in this) {
            if (tag.code == 5 || tag.code == 105) {
                handle = tag.value
                break
            }
        }
        Integer.parseInt(handle, 16)
        return handle
    }

    /**
     * Returns value of first DXFTag(code, value) or default if default != ValueError, else raises ValueError.
     */
    def findFirst(int code, def default_ = null) {
        for (tag in this)
           if (tag.code == code)
               return tag.value
        if (default_ == null)
            throw new NoSuchElementException(String.valueOf(code))
        return default_
    }

    /**
     * Update first existing DXFTag(code, value) or append a new DXFTag(code, value).
     */
    def setFirst(int code, def value) {
        try {
            update(code, value)
        } catch (e) {
            this << new DXFTag(code, value)
        }
    }

    /**
     * Update first existing tag, raises ValueError if tag not exists.
     */
    def update(int code, def value) {
        def index = tagIndex(code)
        set(index, new DXFTag(code, value))
    }

    /**
     * Return first index of DXFTag(code, value).
     */
    def tagIndex(int code, Integer start = 0, Integer end = null) {
        if (!end)
            end = this.size()
        def index = start
        while (index < end) {
            if (get(index).code == code) {
                return index
            }
            index++
        }
        throw new ArrayIndexOutOfBoundsException(code)
    }

    void removeTags(List<Integer> codes) {
        removeAll(findAll { codes.contains(it.code) })
    }

    static Tags fromText(String text) {
        return new Tags(Tagger.skipComments(Tagger.stringTagger(text)))
    }
}
