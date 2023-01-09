var weapons = search.luceneSearch("PATH:\"/app:company_home/cm:Sites/cm:Weapons/*\" +TYPE:\"{http://www.game.com/model/content/1.0}weapon\"");

if (weapons == null || weapons.length == 0) {
	status.code = 404;
	status.message = "There are no weapons";
	status.redirect = true;
} else {
	var unblockedWeapons = new Array();
	var j = 0;
	
	for (i=0; i<weapons.length; i++) {
		if (!weapons[i].properties["{http://www.game.com/model/content/1.0}isBlocked"]) {
			unblockedWeapons[j] = weapons[i];
			j++;
		}
	}
	
	model.weapons = unblockedWeapons;
}
