package net.cassiolandim.dxf.lldxf

class Tagger {

    /**
     * Generates DXFTag() from trusted (internal) source - relies on
     * well formed and error free DXF format. Does not skip comment
     * tags 999.
     */
    static List<DXFTag> stringTagger(String s) {
        def list = new ArrayList<DXFTag>()
        def lines = new ArrayList<String>(s.split('\n') as List)

        int pos = 0

        def nextTag = {
            return new DXFTag(Integer.parseInt(lines[pos].trim()), lines[pos + 1])
        }

        def DUMMY_TAG = new DXFTag(999, '')

        def count = lines.size()
        while (pos < count) {
            def x = nextTag()
            pos += 2
            def code = x.code
            if (Types.isPointCode(code)) {
                def y = nextTag() // y coordinate is mandatory - string_tagger relies on well formed DXF strings
                pos += 2

                def z
                if (pos < count) {
                    z = nextTag() // z coordinate just for 3d points
                } else { // if string s ends with a 2d point
                    z = DUMMY_TAG
                }

                def point
                if (z.code == code + 20) {
                    pos += 2
                    point = [Float.parseFloat(x.value), Float.parseFloat(y.value), Float.parseFloat(z.value)]
                } else {
                    point = [Float.parseFloat(x.value), Float.parseFloat(y.value)]
                }
                list << new DXFTag(code, point)
            } else {
                list << Types.castTag(x)
            }
        }

        return list
    }

    static List<DXFTag> streamTagger(final FileReader stream) {
        def list = new ArrayList<DXFTag>()
        long counter = 0
        DXFTag undoTag = null

        Closure<DXFTag> nextTag = {
            def code = stream.readLine()
            stream.readLine()
            def value = stream.readLine()
            stream.readLine()
            counter += 2
            if (code != null && value != null) // empty strings indicates EOF
                return new DXFTag(Integer.parseInt(code.trim()), value) // without '\n'
            else // missing '\n' indicates EOF
                throw new EOFError()
        }

        while (true) {
            try {
                DXFTag x
                if (undoTag) {
                    x = undoTag
                    undoTag = null
                } else {
                    x = nextTag()
                }

                def code = x.code
                if (Types.isPointCode(code)) {
                    DXFTag y = nextTag()  // y coordinate is mandatory
                    if (y.code != code + 10) {
                        throw new DXFStructureError("Missing required y coordinate near line: ${counter}.")
                    }
                    DXFTag z = nextTag() // z coordinate just for 3d points
                    def point
                    try {
                        if (z.code == code + 20) {
                            point = [Float.parseFloat(x.value), Float.parseFloat(y.value), Float.parseFloat(z.value)]
                        } else {
                            point = [Float.parseFloat(x.value), Float.parseFloat(y.value)]
                            undoTag = z
                        }
                    } catch (NumberFormatException e) {
                        throw new DXFStructureError("Invalid floating point values near line: ${counter}")
                    }
                    list << new DXFTag(code, point)
                } else { //just a single tag
                    try {
                        list << Types.castTag(x)
                    } catch (NumberFormatException e) {
                        throw new  DXFStructureError("Invalid tag (code=${x.code}, value=${x.value}) near line: ${counter}.")
                    }
                }
            } catch (EOFError e) {
                break
            }
        }

        return list
    }

    public static List<DXFTag> skipComments(List<DXFTag> tags, def comments = []) {
        def result = new ArrayList<DXFTag>()
        tags.each {
            if (it.code != 999) result << it
            else comments << it.value
        }
        return result
    }

    public static class EOFError extends RuntimeException {

    }

    public static class DXFStructureError extends RuntimeException {
        DXFStructureError(String message) {
            super(message)
        }
    }
}
