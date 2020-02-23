/* Copyright (c) 2013 National Research Institute for Earth Science and
 * Disaster Prevention (NIED).
 * This code is licensed under the GPL version 3 license, available at the root
 * application directory.
 */
package jp.ecom_plat.saigaitask.entity.names;

import javax.annotation.Generated;

import jp.ecom_plat.saigaitask.entity.EntityNameInterface;
import jp.ecom_plat.saigaitask.entity.db.PubliccommonsReportDataLastDamage;
import jp.ecom_plat.saigaitask.entity.names.PubliccommonsReportDataNames._PubliccommonsReportDataNames;

import org.seasar.extension.jdbc.name.PropertyName;

/**
 * {@link PubliccommonsReportDataLastDamage}のプロパティ名の集合です。
 *
 */
@Generated(value = {"S2JDBC-Gen 2.4.45", "org.seasar.extension.jdbc.gen.internal.model.NamesModelFactoryImpl"}, date = "2014/04/07 14:07:42")
public class PubliccommonsReportDataLastDamageNames implements EntityNameInterface {

	@Override
	public String entityName() {
		return "L-Alert damage info last sending history data";
	}

    /**
     * idのプロパティ名を返します。
     *
     * @return idのプロパティ名
     */
    public static PropertyName<Long> id() {
        return new PropertyName<Long>("id");
    }

    /**
     * pcommonsreportdataidのプロパティ名を返します。
     *
     * @return pcommonsreportdataidのプロパティ名
     */
    public static PropertyName<Long> pcommonsreportdataid() {
        return new PropertyName<Long>("pcommonsreportdataid");
    }

    /**
     * remarksのプロパティ名を返します。
     *
     * @return remarksのプロパティ名
     */
    public static PropertyName<String> remarks() {
        return new PropertyName<String>("remarks");
    }

    /**
     * deadpeopleのプロパティ名を返します。
     *
     * @return deadpeopleのプロパティ名
     */
    public static PropertyName<String> deadpeople() {
        return new PropertyName<String>("deadpeople");
    }

    /**
     * missingpeopleのプロパティ名を返します。
     *
     * @return missingpeopleのプロパティ名
     */
    public static PropertyName<String> missingpeople() {
        return new PropertyName<String>("missingpeople");
    }

    /**
     * seriouslyinjuredpeopleのプロパティ名を返します。
     *
     * @return seriouslyinjuredpeopleのプロパティ名
     */
    public static PropertyName<String> seriouslyinjuredpeople() {
        return new PropertyName<String>("seriouslyinjuredpeople");
    }

    /**
     * slightlyinjuredpeopleのプロパティ名を返します。
     *
     * @return slightlyinjuredpeopleのプロパティ名
     */
    public static PropertyName<String> slightlyinjuredpeople() {
        return new PropertyName<String>("slightlyinjuredpeople");
    }

    /**
     * totalcollapsebuildingのプロパティ名を返します。
     *
     * @return totalcollapsebuildingのプロパティ名
     */
    public static PropertyName<String> totalcollapsebuilding() {
        return new PropertyName<String>("totalcollapsebuilding");
    }

    /**
     * totalcollapsehouseholdのプロパティ名を返します。
     *
     * @return totalcollapsehouseholdのプロパティ名
     */
    public static PropertyName<String> totalcollapsehousehold() {
        return new PropertyName<String>("totalcollapsehousehold");
    }

    /**
     * totalcollapsehumanのプロパティ名を返します。
     *
     * @return totalcollapsehumanのプロパティ名
     */
    public static PropertyName<String> totalcollapsehuman() {
        return new PropertyName<String>("totalcollapsehuman");
    }

    /**
     * halfcollapsebuildingのプロパティ名を返します。
     *
     * @return halfcollapsebuildingのプロパティ名
     */
    public static PropertyName<String> halfcollapsebuilding() {
        return new PropertyName<String>("halfcollapsebuilding");
    }

    /**
     * halfcollapsehouseholdのプロパティ名を返します。
     *
     * @return halfcollapsehouseholdのプロパティ名
     */
    public static PropertyName<String> halfcollapsehousehold() {
        return new PropertyName<String>("halfcollapsehousehold");
    }

    /**
     * halfcollapsehumanのプロパティ名を返します。
     *
     * @return halfcollapsehumanのプロパティ名
     */
    public static PropertyName<String> halfcollapsehuman() {
        return new PropertyName<String>("halfcollapsehuman");
    }

    /**
     * somecollapsebuildingのプロパティ名を返します。
     *
     * @return somecollapsebuildingのプロパティ名
     */
    public static PropertyName<String> somecollapsebuilding() {
        return new PropertyName<String>("somecollapsebuilding");
    }

    /**
     * somecollapsehouseholdのプロパティ名を返します。
     *
     * @return somecollapsehouseholdのプロパティ名
     */
    public static PropertyName<String> somecollapsehousehold() {
        return new PropertyName<String>("somecollapsehousehold");
    }

    /**
     * somecollapsehumanのプロパティ名を返します。
     *
     * @return somecollapsehumanのプロパティ名
     */
    public static PropertyName<String> somecollapsehuman() {
        return new PropertyName<String>("somecollapsehuman");
    }

    /**
     * overinundationbuildingのプロパティ名を返します。
     *
     * @return overinundationbuildingのプロパティ名
     */
    public static PropertyName<String> overinundationbuilding() {
        return new PropertyName<String>("overinundationbuilding");
    }

    /**
     * overinundationhouseholdのプロパティ名を返します。
     *
     * @return overinundationhouseholdのプロパティ名
     */
    public static PropertyName<String> overinundationhousehold() {
        return new PropertyName<String>("overinundationhousehold");
    }

    /**
     * overinundationhumanのプロパティ名を返します。
     *
     * @return overinundationhumanのプロパティ名
     */
    public static PropertyName<String> overinundationhuman() {
        return new PropertyName<String>("overinundationhuman");
    }

    /**
     * underinundationbuildingのプロパティ名を返します。
     *
     * @return underinundationbuildingのプロパティ名
     */
    public static PropertyName<String> underinundationbuilding() {
        return new PropertyName<String>("underinundationbuilding");
    }

    /**
     * underinundationhouseholdのプロパティ名を返します。
     *
     * @return underinundationhouseholdのプロパティ名
     */
    public static PropertyName<String> underinundationhousehold() {
        return new PropertyName<String>("underinundationhousehold");
    }

    /**
     * underinundationhumanのプロパティ名を返します。
     *
     * @return underinundationhumanのプロパティ名
     */
    public static PropertyName<String> underinundationhuman() {
        return new PropertyName<String>("underinundationhuman");
    }

    /**
     * publicbuildingのプロパティ名を返します。
     *
     * @return publicbuildingのプロパティ名
     */
    public static PropertyName<String> publicbuilding() {
        return new PropertyName<String>("publicbuilding");
    }

    /**
     * otherbuildingのプロパティ名を返します。
     *
     * @return otherbuildingのプロパティ名
     */
    public static PropertyName<String> otherbuilding() {
        return new PropertyName<String>("otherbuilding");
    }

    /**
     * ricefieldoutflowburiedのプロパティ名を返します。
     *
     * @return ricefieldoutflowburiedのプロパティ名
     */
    public static PropertyName<String> ricefieldoutflowburied() {
        return new PropertyName<String>("ricefieldoutflowburied");
    }

    /**
     * ricefieldfloodのプロパティ名を返します。
     *
     * @return ricefieldfloodのプロパティ名
     */
    public static PropertyName<String> ricefieldflood() {
        return new PropertyName<String>("ricefieldflood");
    }

    /**
     * fieldoutflowburiedのプロパティ名を返します。
     *
     * @return fieldoutflowburiedのプロパティ名
     */
    public static PropertyName<String> fieldoutflowburied() {
        return new PropertyName<String>("fieldoutflowburied");
    }

    /**
     * fieldfloodのプロパティ名を返します。
     *
     * @return fieldfloodのプロパティ名
     */
    public static PropertyName<String> fieldflood() {
        return new PropertyName<String>("fieldflood");
    }

    /**
     * educationalfacilitiesのプロパティ名を返します。
     *
     * @return educationalfacilitiesのプロパティ名
     */
    public static PropertyName<String> educationalfacilities() {
        return new PropertyName<String>("educationalfacilities");
    }

    /**
     * hospitalのプロパティ名を返します。
     *
     * @return hospitalのプロパティ名
     */
    public static PropertyName<String> hospital() {
        return new PropertyName<String>("hospital");
    }

    /**
     * roadのプロパティ名を返します。
     *
     * @return roadのプロパティ名
     */
    public static PropertyName<String> road() {
        return new PropertyName<String>("road");
    }

    /**
     * bridgeのプロパティ名を返します。
     *
     * @return bridgeのプロパティ名
     */
    public static PropertyName<String> bridge() {
        return new PropertyName<String>("bridge");
    }

    /**
     * riverのプロパティ名を返します。
     *
     * @return riverのプロパティ名
     */
    public static PropertyName<String> river() {
        return new PropertyName<String>("river");
    }

    /**
     * portのプロパティ名を返します。
     *
     * @return portのプロパティ名
     */
    public static PropertyName<String> port() {
        return new PropertyName<String>("port");
    }

    /**
     * sedimentcontrolのプロパティ名を返します。
     *
     * @return sedimentcontrolのプロパティ名
     */
    public static PropertyName<String> sedimentcontrol() {
        return new PropertyName<String>("sedimentcontrol");
    }

    /**
     * cleaningfacilityのプロパティ名を返します。
     *
     * @return cleaningfacilityのプロパティ名
     */
    public static PropertyName<String> cleaningfacility() {
        return new PropertyName<String>("cleaningfacility");
    }

    /**
     * cliffcollapseのプロパティ名を返します。
     *
     * @return cliffcollapseのプロパティ名
     */
    public static PropertyName<String> cliffcollapse() {
        return new PropertyName<String>("cliffcollapse");
    }

    /**
     * railwayinterruptionのプロパティ名を返します。
     *
     * @return railwayinterruptionのプロパティ名
     */
    public static PropertyName<String> railwayinterruption() {
        return new PropertyName<String>("railwayinterruption");
    }

    /**
     * shipのプロパティ名を返します。
     *
     * @return shipのプロパティ名
     */
    public static PropertyName<String> ship() {
        return new PropertyName<String>("ship");
    }

    /**
     * waterのプロパティ名を返します。
     *
     * @return waterのプロパティ名
     */
    public static PropertyName<String> water() {
        return new PropertyName<String>("water");
    }

    /**
     * phoneのプロパティ名を返します。
     *
     * @return phoneのプロパティ名
     */
    public static PropertyName<String> phone() {
        return new PropertyName<String>("phone");
    }

    /**
     * electricのプロパティ名を返します。
     *
     * @return electricのプロパティ名
     */
    public static PropertyName<String> electric() {
        return new PropertyName<String>("electric");
    }

    /**
     * gasのプロパティ名を返します。
     *
     * @return gasのプロパティ名
     */
    public static PropertyName<String> gas() {
        return new PropertyName<String>("gas");
    }

    /**
     * blockwalls_etcのプロパティ名を返します。
     *
     * @return blockwalls_etcのプロパティ名
     */
    public static PropertyName<String> blockwalls_etc() {
        return new PropertyName<String>("blockwalls_etc");
    }

    /**
     * suffererhouseholdのプロパティ名を返します。
     *
     * @return suffererhouseholdのプロパティ名
     */
    public static PropertyName<String> suffererhousehold() {
        return new PropertyName<String>("suffererhousehold");
    }

    /**
     * suffererhumanのプロパティ名を返します。
     *
     * @return suffererhumanのプロパティ名
     */
    public static PropertyName<String> suffererhuman() {
        return new PropertyName<String>("suffererhuman");
    }

    /**
     * firebuildingのプロパティ名を返します。
     *
     * @return firebuildingのプロパティ名
     */
    public static PropertyName<String> firebuilding() {
        return new PropertyName<String>("firebuilding");
    }

    /**
     * firedangerousgoodsのプロパティ名を返します。
     *
     * @return firedangerousgoodsのプロパティ名
     */
    public static PropertyName<String> firedangerousgoods() {
        return new PropertyName<String>("firedangerousgoods");
    }

    /**
     * otherfireのプロパティ名を返します。
     *
     * @return otherfireのプロパティ名
     */
    public static PropertyName<String> otherfire() {
        return new PropertyName<String>("otherfire");
    }

    /**
     * publicscoolfacillitiesのプロパティ名を返します。
     *
     * @return publicscoolfacillitiesのプロパティ名
     */
    public static PropertyName<String> publicscoolfacillities() {
        return new PropertyName<String>("publicscoolfacillities");
    }

    /**
     * agriculturefacilitiesのプロパティ名を返します。
     *
     * @return agriculturefacilitiesのプロパティ名
     */
    public static PropertyName<String> agriculturefacilities() {
        return new PropertyName<String>("agriculturefacilities");
    }

    /**
     * publicengineeringfacilitiesのプロパティ名を返します。
     *
     * @return publicengineeringfacilitiesのプロパティ名
     */
    public static PropertyName<String> publicengineeringfacilities() {
        return new PropertyName<String>("publicengineeringfacilities");
    }

    /**
     * subtotaldamagefacilitiesのプロパティ名を返します。
     *
     * @return subtotaldamagefacilitiesのプロパティ名
     */
    public static PropertyName<String> subtotaldamagefacilities() {
        return new PropertyName<String>("subtotaldamagefacilities");
    }

    /**
     * farmingdamageのプロパティ名を返します。
     *
     * @return farmingdamageのプロパティ名
     */
    public static PropertyName<String> farmingdamage() {
        return new PropertyName<String>("farmingdamage");
    }

    /**
     * forestrydamageのプロパティ名を返します。
     *
     * @return forestrydamageのプロパティ名
     */
    public static PropertyName<String> forestrydamage() {
        return new PropertyName<String>("forestrydamage");
    }

    /**
     * animaldamageのプロパティ名を返します。
     *
     * @return animaldamageのプロパティ名
     */
    public static PropertyName<String> animaldamage() {
        return new PropertyName<String>("animaldamage");
    }

    /**
     * fisheriesdamageのプロパティ名を返します。
     *
     * @return fisheriesdamageのプロパティ名
     */
    public static PropertyName<String> fisheriesdamage() {
        return new PropertyName<String>("fisheriesdamage");
    }

    /**
     * commerceandindustrydamageのプロパティ名を返します。
     *
     * @return commerceandindustrydamageのプロパティ名
     */
    public static PropertyName<String> commerceandindustrydamage() {
        return new PropertyName<String>("commerceandindustrydamage");
    }

    /**
     * otherdamageotherのプロパティ名を返します。
     *
     * @return otherdamageotherのプロパティ名
     */
    public static PropertyName<String> otherdamageother() {
        return new PropertyName<String>("otherdamageother");
    }

    /**
     * totaldamageのプロパティ名を返します。
     *
     * @return totaldamageのプロパティ名
     */
    public static PropertyName<String> totaldamage() {
        return new PropertyName<String>("totaldamage");
    }

    /**
     * schoolmountのプロパティ名を返します。
     *
     * @return schoolmountのプロパティ名
     */
    public static PropertyName<String> schoolmount() {
        return new PropertyName<String>("schoolmount");
    }

    /**
     * farmmountのプロパティ名を返します。
     *
     * @return farmmountのプロパティ名
     */
    public static PropertyName<String> farmmount() {
        return new PropertyName<String>("farmmount");
    }

    /**
     * subtotalotherdamageのプロパティ名を返します。
     *
     * @return subtotalotherdamageのプロパティ名
     */
    public static PropertyName<String> subtotalotherdamage() {
        return new PropertyName<String>("subtotalotherdamage");
    }

    /**
     * fireman1のプロパティ名を返します。
     *
     * @return fireman1のプロパティ名
     */
    public static PropertyName<String> fireman1() {
        return new PropertyName<String>("fireman1");
    }

    /**
     * fireman2のプロパティ名を返します。
     *
     * @return fireman2のプロパティ名
     */
    public static PropertyName<String> fireman2() {
        return new PropertyName<String>("fireman2");
    }

    /**
     * publiccommonsReportDataのプロパティ名を返します。
     *
     * @return publiccommonsReportDataのプロパティ名
     */
    public static _PubliccommonsReportDataNames publiccommonsReportData() {
        return new _PubliccommonsReportDataNames("publiccommonsReportData");
    }

    /**
     * @author S2JDBC-Gen
     */
    public static class _PubliccommonsReportDataLastDamageNames extends PropertyName<PubliccommonsReportDataLastDamage> {

        /**
         * インスタンスを構築します。
         */
        public _PubliccommonsReportDataLastDamageNames() {
        }

        /**
         * インスタンスを構築します。
         *
         * @param name
         *            名前
         */
        public _PubliccommonsReportDataLastDamageNames(final String name) {
            super(name);
        }

        /**
         * インスタンスを構築します。
         *
         * @param parent
         *            親
         * @param name
         *            名前
         */
        public _PubliccommonsReportDataLastDamageNames(final PropertyName<?> parent, final String name) {
            super(parent, name);
        }

        /**
         * idのプロパティ名を返します。
         *
         * @return idのプロパティ名
         */
        public PropertyName<Long> id() {
            return new PropertyName<Long>(this, "id");
        }

        /**
         * pcommonsreportdataidのプロパティ名を返します。
         *
         * @return pcommonsreportdataidのプロパティ名
         */
        public PropertyName<Long> pcommonsreportdataid() {
            return new PropertyName<Long>(this, "pcommonsreportdataid");
        }

        /**
         * remarksのプロパティ名を返します。
         *
         * @return remarksのプロパティ名
         */
        public PropertyName<String> remarks() {
            return new PropertyName<String>(this, "remarks");
        }

        /**
         * deadpeopleのプロパティ名を返します。
         *
         * @return deadpeopleのプロパティ名
         */
        public PropertyName<String> deadpeople() {
            return new PropertyName<String>(this, "deadpeople");
        }

        /**
         * missingpeopleのプロパティ名を返します。
         *
         * @return missingpeopleのプロパティ名
         */
        public PropertyName<String> missingpeople() {
            return new PropertyName<String>(this, "missingpeople");
        }

        /**
         * seriouslyinjuredpeopleのプロパティ名を返します。
         *
         * @return seriouslyinjuredpeopleのプロパティ名
         */
        public PropertyName<String> seriouslyinjuredpeople() {
            return new PropertyName<String>(this, "seriouslyinjuredpeople");
        }

        /**
         * slightlyinjuredpeopleのプロパティ名を返します。
         *
         * @return slightlyinjuredpeopleのプロパティ名
         */
        public PropertyName<String> slightlyinjuredpeople() {
            return new PropertyName<String>(this, "slightlyinjuredpeople");
        }

        /**
         * totalcollapsebuildingのプロパティ名を返します。
         *
         * @return totalcollapsebuildingのプロパティ名
         */
        public PropertyName<String> totalcollapsebuilding() {
            return new PropertyName<String>(this, "totalcollapsebuilding");
        }

        /**
         * totalcollapsehouseholdのプロパティ名を返します。
         *
         * @return totalcollapsehouseholdのプロパティ名
         */
        public PropertyName<String> totalcollapsehousehold() {
            return new PropertyName<String>(this, "totalcollapsehousehold");
        }

        /**
         * totalcollapsehumanのプロパティ名を返します。
         *
         * @return totalcollapsehumanのプロパティ名
         */
        public PropertyName<String> totalcollapsehuman() {
            return new PropertyName<String>(this, "totalcollapsehuman");
        }

        /**
         * halfcollapsebuildingのプロパティ名を返します。
         *
         * @return halfcollapsebuildingのプロパティ名
         */
        public PropertyName<String> halfcollapsebuilding() {
            return new PropertyName<String>(this, "halfcollapsebuilding");
        }

        /**
         * halfcollapsehouseholdのプロパティ名を返します。
         *
         * @return halfcollapsehouseholdのプロパティ名
         */
        public PropertyName<String> halfcollapsehousehold() {
            return new PropertyName<String>(this, "halfcollapsehousehold");
        }

        /**
         * halfcollapsehumanのプロパティ名を返します。
         *
         * @return halfcollapsehumanのプロパティ名
         */
        public PropertyName<String> halfcollapsehuman() {
            return new PropertyName<String>(this, "halfcollapsehuman");
        }

        /**
         * somecollapsebuildingのプロパティ名を返します。
         *
         * @return somecollapsebuildingのプロパティ名
         */
        public PropertyName<String> somecollapsebuilding() {
            return new PropertyName<String>(this, "somecollapsebuilding");
        }

        /**
         * somecollapsehouseholdのプロパティ名を返します。
         *
         * @return somecollapsehouseholdのプロパティ名
         */
        public PropertyName<String> somecollapsehousehold() {
            return new PropertyName<String>(this, "somecollapsehousehold");
        }

        /**
         * somecollapsehumanのプロパティ名を返します。
         *
         * @return somecollapsehumanのプロパティ名
         */
        public PropertyName<String> somecollapsehuman() {
            return new PropertyName<String>(this, "somecollapsehuman");
        }

        /**
         * overinundationbuildingのプロパティ名を返します。
         *
         * @return overinundationbuildingのプロパティ名
         */
        public PropertyName<String> overinundationbuilding() {
            return new PropertyName<String>(this, "overinundationbuilding");
        }

        /**
         * overinundationhouseholdのプロパティ名を返します。
         *
         * @return overinundationhouseholdのプロパティ名
         */
        public PropertyName<String> overinundationhousehold() {
            return new PropertyName<String>(this, "overinundationhousehold");
        }

        /**
         * overinundationhumanのプロパティ名を返します。
         *
         * @return overinundationhumanのプロパティ名
         */
        public PropertyName<String> overinundationhuman() {
            return new PropertyName<String>(this, "overinundationhuman");
        }

        /**
         * underinundationbuildingのプロパティ名を返します。
         *
         * @return underinundationbuildingのプロパティ名
         */
        public PropertyName<String> underinundationbuilding() {
            return new PropertyName<String>(this, "underinundationbuilding");
        }

        /**
         * underinundationhouseholdのプロパティ名を返します。
         *
         * @return underinundationhouseholdのプロパティ名
         */
        public PropertyName<String> underinundationhousehold() {
            return new PropertyName<String>(this, "underinundationhousehold");
        }

        /**
         * underinundationhumanのプロパティ名を返します。
         *
         * @return underinundationhumanのプロパティ名
         */
        public PropertyName<String> underinundationhuman() {
            return new PropertyName<String>(this, "underinundationhuman");
        }

        /**
         * publicbuildingのプロパティ名を返します。
         *
         * @return publicbuildingのプロパティ名
         */
        public PropertyName<String> publicbuilding() {
            return new PropertyName<String>(this, "publicbuilding");
        }

        /**
         * otherbuildingのプロパティ名を返します。
         *
         * @return otherbuildingのプロパティ名
         */
        public PropertyName<String> otherbuilding() {
            return new PropertyName<String>(this, "otherbuilding");
        }

        /**
         * ricefieldoutflowburiedのプロパティ名を返します。
         *
         * @return ricefieldoutflowburiedのプロパティ名
         */
        public PropertyName<String> ricefieldoutflowburied() {
            return new PropertyName<String>(this, "ricefieldoutflowburied");
        }

        /**
         * ricefieldfloodのプロパティ名を返します。
         *
         * @return ricefieldfloodのプロパティ名
         */
        public PropertyName<String> ricefieldflood() {
            return new PropertyName<String>(this, "ricefieldflood");
        }

        /**
         * fieldoutflowburiedのプロパティ名を返します。
         *
         * @return fieldoutflowburiedのプロパティ名
         */
        public PropertyName<String> fieldoutflowburied() {
            return new PropertyName<String>(this, "fieldoutflowburied");
        }

        /**
         * fieldfloodのプロパティ名を返します。
         *
         * @return fieldfloodのプロパティ名
         */
        public PropertyName<String> fieldflood() {
            return new PropertyName<String>(this, "fieldflood");
        }

        /**
         * educationalfacilitiesのプロパティ名を返します。
         *
         * @return educationalfacilitiesのプロパティ名
         */
        public PropertyName<String> educationalfacilities() {
            return new PropertyName<String>(this, "educationalfacilities");
        }

        /**
         * hospitalのプロパティ名を返します。
         *
         * @return hospitalのプロパティ名
         */
        public PropertyName<String> hospital() {
            return new PropertyName<String>(this, "hospital");
        }

        /**
         * roadのプロパティ名を返します。
         *
         * @return roadのプロパティ名
         */
        public PropertyName<String> road() {
            return new PropertyName<String>(this, "road");
        }

        /**
         * bridgeのプロパティ名を返します。
         *
         * @return bridgeのプロパティ名
         */
        public PropertyName<String> bridge() {
            return new PropertyName<String>(this, "bridge");
        }

        /**
         * riverのプロパティ名を返します。
         *
         * @return riverのプロパティ名
         */
        public PropertyName<String> river() {
            return new PropertyName<String>(this, "river");
        }

        /**
         * portのプロパティ名を返します。
         *
         * @return portのプロパティ名
         */
        public PropertyName<String> port() {
            return new PropertyName<String>(this, "port");
        }

        /**
         * sedimentcontrolのプロパティ名を返します。
         *
         * @return sedimentcontrolのプロパティ名
         */
        public PropertyName<String> sedimentcontrol() {
            return new PropertyName<String>(this, "sedimentcontrol");
        }

        /**
         * cleaningfacilityのプロパティ名を返します。
         *
         * @return cleaningfacilityのプロパティ名
         */
        public PropertyName<String> cleaningfacility() {
            return new PropertyName<String>(this, "cleaningfacility");
        }

        /**
         * cliffcollapseのプロパティ名を返します。
         *
         * @return cliffcollapseのプロパティ名
         */
        public PropertyName<String> cliffcollapse() {
            return new PropertyName<String>(this, "cliffcollapse");
        }

        /**
         * railwayinterruptionのプロパティ名を返します。
         *
         * @return railwayinterruptionのプロパティ名
         */
        public PropertyName<String> railwayinterruption() {
            return new PropertyName<String>(this, "railwayinterruption");
        }

        /**
         * shipのプロパティ名を返します。
         *
         * @return shipのプロパティ名
         */
        public PropertyName<String> ship() {
            return new PropertyName<String>(this, "ship");
        }

        /**
         * waterのプロパティ名を返します。
         *
         * @return waterのプロパティ名
         */
        public PropertyName<String> water() {
            return new PropertyName<String>(this, "water");
        }

        /**
         * phoneのプロパティ名を返します。
         *
         * @return phoneのプロパティ名
         */
        public PropertyName<String> phone() {
            return new PropertyName<String>(this, "phone");
        }

        /**
         * electricのプロパティ名を返します。
         *
         * @return electricのプロパティ名
         */
        public PropertyName<String> electric() {
            return new PropertyName<String>(this, "electric");
        }

        /**
         * gasのプロパティ名を返します。
         *
         * @return gasのプロパティ名
         */
        public PropertyName<String> gas() {
            return new PropertyName<String>(this, "gas");
        }

        /**
         * blockwalls_etcのプロパティ名を返します。
         *
         * @return blockwalls_etcのプロパティ名
         */
        public PropertyName<String> blockwalls_etc() {
            return new PropertyName<String>(this, "blockwalls_etc");
        }

        /**
         * suffererhouseholdのプロパティ名を返します。
         *
         * @return suffererhouseholdのプロパティ名
         */
        public PropertyName<String> suffererhousehold() {
            return new PropertyName<String>(this, "suffererhousehold");
        }

        /**
         * suffererhumanのプロパティ名を返します。
         *
         * @return suffererhumanのプロパティ名
         */
        public PropertyName<String> suffererhuman() {
            return new PropertyName<String>(this, "suffererhuman");
        }

        /**
         * firebuildingのプロパティ名を返します。
         *
         * @return firebuildingのプロパティ名
         */
        public PropertyName<String> firebuilding() {
            return new PropertyName<String>(this, "firebuilding");
        }

        /**
         * firedangerousgoodsのプロパティ名を返します。
         *
         * @return firedangerousgoodsのプロパティ名
         */
        public PropertyName<String> firedangerousgoods() {
            return new PropertyName<String>(this, "firedangerousgoods");
        }

        /**
         * otherfireのプロパティ名を返します。
         *
         * @return otherfireのプロパティ名
         */
        public PropertyName<String> otherfire() {
            return new PropertyName<String>(this, "otherfire");
        }

        /**
         * publicscoolfacillitiesのプロパティ名を返します。
         *
         * @return publicscoolfacillitiesのプロパティ名
         */
        public PropertyName<String> publicscoolfacillities() {
            return new PropertyName<String>(this, "publicscoolfacillities");
        }

        /**
         * agriculturefacilitiesのプロパティ名を返します。
         *
         * @return agriculturefacilitiesのプロパティ名
         */
        public PropertyName<String> agriculturefacilities() {
            return new PropertyName<String>(this, "agriculturefacilities");
        }

        /**
         * publicengineeringfacilitiesのプロパティ名を返します。
         *
         * @return publicengineeringfacilitiesのプロパティ名
         */
        public PropertyName<String> publicengineeringfacilities() {
            return new PropertyName<String>(this, "publicengineeringfacilities");
        }

        /**
         * subtotaldamagefacilitiesのプロパティ名を返します。
         *
         * @return subtotaldamagefacilitiesのプロパティ名
         */
        public PropertyName<String> subtotaldamagefacilities() {
            return new PropertyName<String>(this, "subtotaldamagefacilities");
        }

        /**
         * farmingdamageのプロパティ名を返します。
         *
         * @return farmingdamageのプロパティ名
         */
        public PropertyName<String> farmingdamage() {
            return new PropertyName<String>(this, "farmingdamage");
        }

        /**
         * forestrydamageのプロパティ名を返します。
         *
         * @return forestrydamageのプロパティ名
         */
        public PropertyName<String> forestrydamage() {
            return new PropertyName<String>(this, "forestrydamage");
        }

        /**
         * animaldamageのプロパティ名を返します。
         *
         * @return animaldamageのプロパティ名
         */
        public PropertyName<String> animaldamage() {
            return new PropertyName<String>(this, "animaldamage");
        }

        /**
         * fisheriesdamageのプロパティ名を返します。
         *
         * @return fisheriesdamageのプロパティ名
         */
        public PropertyName<String> fisheriesdamage() {
            return new PropertyName<String>(this, "fisheriesdamage");
        }

        /**
         * commerceandindustrydamageのプロパティ名を返します。
         *
         * @return commerceandindustrydamageのプロパティ名
         */
        public PropertyName<String> commerceandindustrydamage() {
            return new PropertyName<String>(this, "commerceandindustrydamage");
        }

        /**
         * otherdamageotherのプロパティ名を返します。
         *
         * @return otherdamageotherのプロパティ名
         */
        public PropertyName<String> otherdamageother() {
            return new PropertyName<String>(this, "otherdamageother");
        }

        /**
         * totaldamageのプロパティ名を返します。
         *
         * @return totaldamageのプロパティ名
         */
        public PropertyName<String> totaldamage() {
            return new PropertyName<String>(this, "totaldamage");
        }

        /**
         * schoolmountのプロパティ名を返します。
         *
         * @return schoolmountのプロパティ名
         */
        public PropertyName<String> schoolmount() {
            return new PropertyName<String>(this, "schoolmount");
        }

        /**
         * farmmountのプロパティ名を返します。
         *
         * @return farmmountのプロパティ名
         */
        public PropertyName<String> farmmount() {
            return new PropertyName<String>(this, "farmmount");
        }

        /**
         * subtotalotherdamageのプロパティ名を返します。
         *
         * @return subtotalotherdamageのプロパティ名
         */
        public PropertyName<String> subtotalotherdamage() {
            return new PropertyName<String>(this, "subtotalotherdamage");
        }

        /**
         * fireman1のプロパティ名を返します。
         *
         * @return fireman1のプロパティ名
         */
        public PropertyName<String> fireman1() {
            return new PropertyName<String>(this, "fireman1");
        }

        /**
         * fireman2のプロパティ名を返します。
         *
         * @return fireman2のプロパティ名
         */
        public PropertyName<String> fireman2() {
            return new PropertyName<String>(this, "fireman2");
        }

        /**
         * publiccommonsReportDataのプロパティ名を返します。
         *
         * @return publiccommonsReportDataのプロパティ名
         */
        public _PubliccommonsReportDataNames publiccommonsReportData() {
            return new _PubliccommonsReportDataNames(this, "publiccommonsReportData");
        }
    }
}
