/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    /**
     * The max image depth level.
     */
    public static final int MAX_DEPTH = 7;
    /**
     * The d0 upper-left longitude.
     */
    private final double d0ULLON = MapServer.ROOT_ULLON;
    /**
     * The d0 upper-left latitude.
     */
    private final double d0ULLAT = MapServer.ROOT_ULLAT;
    /**
     * The d0 lower-right longitude.
     */
    private final double d0LRLON = MapServer.ROOT_LRLON;
    /**
     * The d0 lower-right latitude.
     */
    private final double d0LRLAT = MapServer.ROOT_LRLAT;
    /**
     * The DPP of d0.
     */
    private final double d0DPP = lonDPP(d0LRLON, d0ULLON, 256);

    //lat is row, lon is col.
    private RasterResultParams getMapRasterHelper(double ullon, double ullat,
                                                  double lrlon, double lrlat, double lonDPP) {
        int m = 0;
        double initDPP = d0DPP;
        double currDPP = initDPP;
        int depth;
        while (currDPP >= lonDPP) {
            currDPP = currDPP / 2;
            m += 1;
        }
        if (m > 7) {
            depth = 7;
        } else {
            depth = m;
        }
        double tileLonSide = (d0LRLON - d0ULLON) / Math.pow(2, depth);
        double tileLatSide = (d0ULLAT - d0LRLAT) / Math.pow(2, depth);

        if (lonDPP > initDPP) {
            String[][] rtn = new String[1][1];
            rtn[0][0] = "img/d0_x0_y0.png";
            RasterResultParams.Builder parRec = new RasterResultParams.Builder();
            parRec.setRenderGrid(rtn);
            parRec.setRasterUlLat(d0ULLAT);
            parRec.setRasterUlLon(d0ULLON);
            parRec.setRasterLrLat(d0LRLAT);
            parRec.setRasterLrLon(d0LRLON);
            parRec.setDepth(0);
            parRec.setQuerySuccess(true);
            RasterResultParams rtnn = parRec.create();
            return rtnn;
        } else {
            int latStart = (int) Math.floor((d0ULLAT - ullat) / tileLatSide);
            int lonStart = (int) Math.floor((ullon - d0ULLON) / tileLonSide);
            int latEnd = (int) (Math.pow(2, depth)
                    - Math.floor((lrlat - d0LRLAT) / tileLatSide) - 1);
            int lonEnd = (int) (Math.pow(2, depth)
                    - Math.floor((d0LRLON - lrlon) / tileLonSide) - 1);
            //int latNum = (int) Math.floor((ullat - lrlat) / tileLatSide) + 1;
            //int lonNum = (int) Math.floor((lrlon - ullon) / tileLonSide) + 2;
            int latNum = latEnd - latStart + 1;
            int lonNum = lonEnd - lonStart + 1;
            String[][] rtn = new String[latNum][lonNum];
            for (int i = latStart; i <= latEnd; i++) {
                for (int j = lonStart; j <= lonEnd; j++) {
                    if (latStart < 0 || latEnd > Math.pow(2, depth) - 1 || lonStart < 0
                            || lonEnd > Math.pow(2, depth) - 1) {
                        continue;
                    }
                    rtn[i - latStart][j - lonStart] = "d" + Integer.toString(depth)
                            + "_x" + Integer.toString(j) + "_y" + Integer.toString(i) + ".png";
                    //String name = rtn[i - latStart][j - lonStart];
                    //System.out.println(name);
                }
            }
            RasterResultParams.Builder parRec = new RasterResultParams.Builder();
            parRec.setRenderGrid(rtn);
            /*parRec.setRasterUlLat(ullat);
            parRec.setRasterUlLon(ullon);
            parRec.setRasterLrLat(lrlat);
            parRec.setRasterLrLon(lrlon);**/
            parRec.setRasterUlLat(d0ULLAT - latStart * tileLatSide);
            parRec.setRasterUlLon(d0ULLON + lonStart * tileLonSide);
            parRec.setRasterLrLat(d0ULLAT - (latEnd + 1) * tileLatSide);
            parRec.setRasterLrLon(d0ULLON + (lonEnd + 1) * tileLonSide);
            parRec.setDepth(depth);
            parRec.setQuerySuccess(true);
            RasterResultParams rtnn = parRec.create();
            return rtnn;
        }
    }

    /*public static void main() {
        private static final String PARAMS_FILE = "raster_params.txt";
        List<RasterRequestParams> testParams = paramsFromFile();
        System.out.println(String.format("Running test: %d", 0));
        RasterRequestParams params = testParams.get(0);
        RasterResultParams actual = rasterer.getMapRaster(params);
    }**/

    /**
     * Takes a user query and finds the grid of images that best matches the query. These images
     * will be combined into one big image (rastered) by the front end. The grid of images must obey
     * the following properties, where image in the grid is referred to as a "tile".
     * <ul>
     * <li>The tiles collected must cover the most longitudinal distance per pixel (LonDPP)
     * possible, while still covering less than or equal to the amount of longitudinal distance
     * per pixel in the query box for the user viewport size.</li>
     * <li>Contains all tiles that intersect the query bounding box that fulfill the above
     * condition.</li>
     * <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     * </ul>
     *
     * @param params The RasterRequestParams containing coordinates of the query box and the browser
     *               viewport width and height.
     * @return A valid RasterResultParams containing the computed results.
     */
    public RasterResultParams getMapRaster(RasterRequestParams params) {
        System.out.println(
                "Since you haven't implemented getMapRaster, nothing is displayed in the browser.");

        /* ///
         * Hint: Define additional classes to make it easier to pass around multiple values, and
         * define additional methods to make it easier to test and reason about code. */
        /** The requested upper-left latitude. */
        double requestUllat = params.ullat;
        /** The requested upper-left longitude. */
        double requestUllon = params.ullon;
        /** The requested lower-right latitude. */
        double requestLrlat = params.lrlat;
        /** The requested lower-right longitude. */
        double requestLrlon = params.lrlon;

        /** The width (in pixels) of the browser viewport. */
        double w = params.w;

        /**longitudinal distance per pixel of query box*/
        double requestLonDpp = lonDPP(requestLrlon, requestUllon, w);

        if (requestUllat < requestLrlat || requestUllon > requestLrlon) {
            return RasterResultParams.queryFailed();
        }
        if (requestUllat > d0ULLAT || requestUllon < d0ULLON
                || requestLrlat < d0LRLAT || requestLrlon > d0LRLON) {
            return RasterResultParams.queryFailed();
        }

        RasterResultParams ret = getMapRasterHelper(requestUllon, requestUllat,
                requestLrlon, requestLrlat, requestLonDpp);
        return ret;
    }

    /**
     * Calculates the lonDPP of an image or query box
     *
     * @param lrlon Lower right longitudinal value of the image or query box
     * @param ullon Upper left longitudinal value of the image or query box
     * @param width Width of the query box or image
     * @return lonDPP
     */
    private double lonDPP(double lrlon, double ullon, double width) {
        return (lrlon - ullon) / width;
    }
}
