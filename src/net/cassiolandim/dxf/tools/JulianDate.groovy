package net.cassiolandim.dxf.tools

class JulianDate {

    Date date
    float result

    private JulianDate(Date date) {
        this.date = date
        this.result = julianDate() + fractionalDay()
    }

    private float julianDate() {
        def cal = Calendar.getInstance()
        cal.setTime(date)
        def y = cal.get(Calendar.YEAR) + (cal.get(Calendar.MONTH) - 1.85) / 12.0
        def A = Math.floor(367.0 * y) - 1.75 * Math.floor(y) + cal.get(Calendar.DAY_OF_MONTH)
        def B = Math.floor(A) - 0.75 * Math.floor(y / 100.0)
        return Math.floor(B) + 1721115.0
    }

    private float fractionalDay() {
        def cal = Calendar.getInstance()
        cal.setTime(date)
        def seconds = cal.get(Calendar.HOUR_OF_DAY) * 3600.0 + cal.get(Calendar.MINUTE) * 60.0 + cal.get(Calendar.SECOND)
        return seconds / 86400.0
    }

    static float fromDate(Date date) {
        return new JulianDate(date).result
    }
}
