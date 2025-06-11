# AreaPlayerControl

AreaPlayerControl is a simple area management plugin for PaperMC servers. It integrates with WorldEdit to save, delete and list selected regions. It also provides an `info` command to display the number of players inside a region.

This version is compatible with PaperMC 1.20.x and 1.21.x.

Commands require the player to have OP permissions.

## Commands

```
/area save <name>   - Save the current WorldEdit selection with the given name.
/area remove <name> - Delete the specified region.
/area info <name>   - Show region coordinates and the number of players inside.
/area list          - List all saved regions.
/area list <name>   - Show the placeholder that can be used for a region.
/area reload        - Reload the configuration.
```

## Configuration

You can change sub command names and customise messages in English or Turkish using `config.yml`. The main command is fixed as `/area`.

## Building (Creating the Jar)

To build the plugin you need Java 21 and Gradle installed. Run:

```bash
gradle build
```

After building, the jar `build/libs/AreaPlayerControl-1.0-SNAPSHOT.jar` will be created. Copy it to your PaperMC server's `plugins/` folder.

WorldEdit must be installed on the server.

If PlaceholderAPI is present, the plugin provides a placeholder `%areaplayercontrol_players_<name>%` which returns the number of players inside that region. You can quickly see the placeholder info using `/area list <name>`.

---

## T\u00FCrk\u00E7e

AreaPlayerControl, PaperMC tabanl\u0131 Minecraft sunucular\u0131 i\u00E7in basit bir alan y\u00F6netim eklentisidir. WorldEdit eklentisinden yararlanarak se\u00E7ti\u011Finiz b\u00F6lgeleri kaydetmenizi, silmenizi ve listelemenizi sa\u011Flar. Ayr\u0131ca kay\u0131tl\u0131 b\u00F6lgeler i\u00E7indeki oyuncu say\u0131s\u0131n\u0131 g\u00F6r\u00FCnt\u00FClemeye yarayan bir `info` komutuna sahiptir.

Bu s\u00FCr\u00FCm 1.20.x ve 1.21.x PaperMC sunucular\u0131yla uyumludur.

Komutlar\u0131n \u00E7al\u0131\u015Fabilmesi i\u00E7in oyuncunun OP yetkisine sahip olmas\u0131 gerekir.

### Komutlar

```
/area save <isim>   - Ge\u00E7erli WorldEdit se\u00E7imini verilen isimle kaydeder.
/area remove <isim> - Belirtilen b\u00F6lgeyi siler.
/area info <isim>   - B\u00F6lge koordinatlar\u0131n\u0131 ve i\u00E7indeki oyuncu say\u0131s\u0131n\u0131 g\u00F6sterir.
/area list          - Kay\u0131tl\u0131 t\u00FCm b\u00F6lgeleri listeler.
/area list <isim>   - B\u00F6lge i\u00E7in kullanabilece\u011Finiz placeholder bilgisini g\u00F6sterir.
/area reload        - Yap\u0131land\u0131rmay\u0131 yeniden y\u00FCkler.
```

### Yap\u0131land\u0131rma

`config.yml` dosyas\u0131yla alt komut adlar\u0131n\u0131 de\u011Fi\u015Ftirebilir, mesajlar\u0131n \u0130ngilizce veya T\u00FCrk\u00E7e \u00E7evirilerini \u00F6zelle\u015Ftirebilirsiniz. Ana komut ismi `/area` olarak sabittir ve yap\u0131land\u0131rma ile de\u011Fi\u015Ftirilemez.

### Derleme (Jar Olu\u015Fturma)

Projenin derlenebilmesi i\u00E7in Java 21 ve Gradle'\u0131n y\u00FCkl\u00FC olmas\u0131 gerekir. A\u015Fa\u011F\u0131daki komutu \u00E7al\u0131\u015Ft\u0131r\u0131n:

```bash
gradle build
```

Derleme tamamland\u0131\u011F\u0131nda `build/libs/AreaPlayerControl-1.0-SNAPSHOT.jar` dosyas\u0131 olu\u015Fur. Bu jar dosyas\u0131n\u0131 PaperMC sunucunuzun `plugins/` klas\u00F6r\u00FCne kopyalayarak eklentiyi kullanmaya ba\u015Flayabilirsiniz.

WorldEdit eklentisinin sunucuda y\u00FCkl\u00FC olmas\u0131 gerekti\u011Fini unutmay\u0131n.

Eklenti, PlaceholderAPI y\u00FCkl\u00FC ise her kay\u0131tl\u0131 b\u00F6lge i\u00E7in `%areaplayercontrol_players_<isim>%` bi\u00E7iminde bir placeholder sa\u011Flar. Bu placeholder ilgili b\u00F6lge i\u00E7indeki \u00E7evrimi\u00E7i oyuncu say\u0131s\u0131n\u0131 d\u00F6nd\u00FCr\u00FCr ve TitleManager gibi eklentiler taraf\u0131ndan skorboardlarda kullan\u0131labilir. Placeholder bilgisini h\u0131zl\u0131ca \u00F6\u011Frenmek i\u00E7in `/area list <isim>` komutunu kullanabilirsiniz.
