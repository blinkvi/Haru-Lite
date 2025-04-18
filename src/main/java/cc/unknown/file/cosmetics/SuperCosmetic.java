package cc.unknown.file.cosmetics;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import lombok.Getter;

@Getter
public class SuperCosmetic {
	private String name;
	private String halo;
	private String hat;
	private String pet;
	private String aura;
	private String wings;
	private String accesories;
	private String cape;
	private LocalDateTime lastUpdated;
	
	public SuperCosmetic(String name, String halo, String hat, String pet, String aura, String wings, String accesories, String cape) {
		this.name = name;
		this.halo = halo;
		this.hat = hat;
		this.pet = pet;
		this.aura = aura;
		this.wings = wings;
		this.accesories = accesories;
		this.cape = cape;
		this.lastUpdated = LocalDateTime.now();
	}

	@Override
	public String toString() {
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

	    return "SuperCosmetic{" +
	            "name='" + name + '\'' +
	            ", halo='" + halo + '\'' +
	            ", hat='" + hat + '\'' +
	            ", pet='" + pet + '\'' +
	            ", aura='" + aura + '\'' +
	            ", wings='" + wings + '\'' +
	            ", accesories='" + accesories + '\'' +
	            ", cape='" + cape + '\'' +
	            ", lastUpdated=" + lastUpdated.format(formatter) +
	            '}';
	}
}
