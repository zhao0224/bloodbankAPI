package bloodbank.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-03-30T18:46:25.327-0400")
@StaticMetamodel(Address.class)
public class Address_ extends PojoBase_ {
	public static volatile SingularAttribute<Address, String> streetNumber;
	public static volatile SingularAttribute<Address, String> city;
	public static volatile SingularAttribute<Address, String> country;
	public static volatile SingularAttribute<Address, String> province;
	public static volatile SingularAttribute<Address, String> street;
	public static volatile SingularAttribute<Address, String> zipcode;
	public static volatile SetAttribute<Address, Contact> contacts;
}
