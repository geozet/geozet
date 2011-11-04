package nl.geozet.openls.client;

import java.util.ArrayList;
import java.util.List;

import nl.geozet.openls.databinding.openls.Address;
import nl.geozet.openls.databinding.openls.GeocodeResponse;
import nl.geozet.openls.databinding.openls.GeocodeResponseList;
import nl.geozet.openls.databinding.openls.GeocodedAddress;
import nl.geozet.openls.databinding.openls.OpenLSConstants;

/**
 * Utility klasse OpenLSClientUtil.
 * 
 * @author strampel@atlis.nl
 */
public final class OpenLSClientUtil {

    /**
     * Instantiates a new open ls client util.
     */
    private OpenLSClientUtil() {
        /* private constructor voor deze utility klasse. */
    }

    /**
     * Gets the geocoded address list.
     * 
     * @param gcr
     *            the gcr
     * @return the geocoded address list
     */
    public static List<GeocodedAddress> getGeocodedAddressList(
            GeocodeResponse gcr) {
        final List<GeocodedAddress> addressList = new ArrayList<GeocodedAddress>();
        for (int i = 0; i < gcr.getGeocodeResponseListSize(); i++) {
            final GeocodeResponseList gcrl = gcr.getGeocodeResponseListAt(i);
            for (int j = 0; j < gcrl.getGeocodedAddressSize(); j++) {
                final GeocodedAddress gca = gcrl.getGeocodedAddressAt(j);
                addressList.add(gca);
            }
        }
        return addressList;
    }

    /**
     * Gets the open ls client address list.
     * 
     * @param gcr
     *            the gcr
     * @return the open ls client address list
     */
    public static List<OpenLSClientAddress> getOpenLSClientAddressList(
            GeocodeResponse gcr) {
        final List<OpenLSClientAddress> addressList = new ArrayList<OpenLSClientAddress>();
        final List<GeocodedAddress> gcal = getGeocodedAddressList(gcr);
        for (int i = 0; i < gcal.size(); i++) {
            final GeocodedAddress gca = gcal.get(i);
            final OpenLSClientAddress addr = new OpenLSClientAddress();

            if (!gca.hasPoint() || (gca.getPoint().getPosSize() == 0)) {
                continue;
            }

            addr.setxCoord((gca.getPoint().getPosAt(0).getX()).toString());
            addr.setyCoord((gca.getPoint().getPosAt(0).getY()).toString());

            if (gca.getAddress() != null) {
                final Address adr = gca.getAddress();
                if (adr.hasStreetAddress()
                        && adr.getStreetAddress().hasStreet()) {
                    addr.setStreetName(adr.getStreetAddress().getStreet()
                            .getStreet());
                }
                if (adr.hasStreetAddress()
                        && adr.getStreetAddress().hasBuilding()
                        && adr.getStreetAddress().getBuilding().hasNumber()) {
                    addr.setStreetNumber(adr.getStreetAddress().getBuilding()
                            .getNumber());
                }
                if (adr.hasPostalCode() && adr.getPostalCode().hasPostalCode()) {
                    addr.setPostalCode(adr.getPostalCode().getPostalCode());
                }
                if (adr.getPlaceByType(OpenLSConstants.PLACE_TYPE_COUNTRYSUBDIVISION) != null) {
                    addr.setCountrySubdivision(adr
                            .getPlaceByType(OpenLSConstants.PLACE_TYPE_COUNTRYSUBDIVISION));
                }
                if (adr.getPlaceByType(OpenLSConstants.PLACE_TYPE_MUNICIPALITY) != null) {
                    addr.setMunicipality(adr
                            .getPlaceByType(OpenLSConstants.PLACE_TYPE_MUNICIPALITY));
                }
                if (adr.getPlaceByType(OpenLSConstants.PLACE_TYPE_MUNICIPALITYSUBDIVISION) != null) {
                    addr.setMunicipalitySubdivision(adr
                            .getPlaceByType(OpenLSConstants.PLACE_TYPE_MUNICIPALITYSUBDIVISION));
                }
            }
            if (addr.isValidClientAddress()) {
                addressList.add(addr);
            }
        }
        return addressList;
    }
}
