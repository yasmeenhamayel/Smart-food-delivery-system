package com.smartdelivery.domain;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Test;

public class AddressTest {
	@Test
	void creatAddressCorrectly(){
		
		String city = "Nablus";
		String area = "Taluza";
		String street = "main street";
		String details = "in front of yellow house";
		Address address = new Address(city , area ,street , details);
		
		
		assertEquals(city , address.getCity());
		assertEquals(area , address.getArea());
		assertEquals(street , address.getStreet());
		assertEquals(details , address.getDetails());
	 }
	
	
	@Test
	void acceptNullDetails() {
		String city = "Nablus";
		String area = "Taluza";
		String street = "main street";
		String details = null;
		Address address = new Address(city , area , street , details);
		
		assertNull( address.getDetails());
	}
	
	@Test
	void acceptBlankDetailsAndStripIt() {
		String city = "  Nablus  ";
		String area = "   Taluza  ";
		String street = "  main street  ";
		String details = "      ";
		Address address = new Address(city ,area , street , details);
		
		assertEquals( "" , address.getDetails());
		
	}
	
	
	@Test
	void stripAddressStrings() {
		String city = "  Nablus  ";
		String area = "   Taluza  ";
		String street = "  main street  ";
		String details = "   in front of yellow house   ";
		Address address = new Address(city ,area , street , details);
		
		assertEquals( "Nablus" , address.getCity());
		assertEquals("Taluza", address.getArea());
		assertEquals("main street", address.getStreet());
		assertEquals("in front of yellow house" , address.getDetails());
	}
	

	@Test
	void RejectsNullCity() {
		String city = null;
		String area = "Taluza";
		String street="main";
		String details="in front of the market";
		
		assertThrows( IllegalArgumentException.class, ()->new Address(city , area , street , details));
	}
	
	
	@Test
	void rejectsBlanlkCity() {
		String city="    " ;
		String area = "Beit Iba";
		String street="main";
		String details="in front of the market";
		
		assertThrows( IllegalArgumentException.class, ()->new Address(city , area , street , details));
	}
	
	@Test
	void rejectsNullArea() {
		String city="Nablus" ;
		String area = null;
		String street="main";
		String details="in front of the market";
		
		assertThrows( IllegalArgumentException.class, ()->new Address(city , area , street , details));
	}
	
	@Test
	void rejectsBlankArea() {
		String city="Nablus" ;
		String area = "    ";
		String street="main";
		String details="in front of the market";
		
		assertThrows( IllegalArgumentException.class, ()->new Address(city , area , street , details));
	}
	
	
	@Test
	void rejectsNullStreet() {
		String city = "Tubas" ;
		String area ="middle";
		String street = null ;
		String details="in front of the market";
		
		assertThrows( IllegalArgumentException.class, ()->new Address(city , area , street , details));
	}
	
	@Test
	void rejectsBlanlkStreet() {
		String city="Tubas" ;
		String area ="middle";
		String street="     ";
		String details="in front of the market";
		
		assertThrows( IllegalArgumentException.class, ()->new Address(city , area , street , details));
	}


}
