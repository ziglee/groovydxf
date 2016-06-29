package net.cassiolandim.dxf.tools

class HandleGenerator {

    int handle

    public HandleGenerator(String startValue = '1') {
        handle = Integer.parseInt(startValue, 16)
    }

    String next() {
        def nextHandle = toString()
        handle++
        return nextHandle.toUpperCase()
    }

    def reset(String value) {
        handle = Integer.parseInt(value, 16)
    }

    @Override
    String toString() {
        return Integer.toString(handle, 16).toUpperCase()
    }
}
