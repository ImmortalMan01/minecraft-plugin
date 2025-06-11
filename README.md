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

## Türkçe

AreaPlayerControl, PaperMC tabanlı Minecraft sunucuları için basit bir alan yönetim eklentisidir. WorldEdit eklentisinden yararlanarak seçtiğiniz bölgeleri kaydetmenizi, silmenizi ve listelemenizi sağlar. Ayrıca kayıtlı bölgeler içindeki oyuncu sayısını görüntülemeye yarayan bir `info` komutuna sahiptir.

Bu sürüm 1.20.x ve 1.21.x PaperMC sunucularıyla uyumludur.

Komutların çalışabilmesi için oyuncunun OP yetkisine sahip olması gerekir.

### Komutlar

```
/area save <isim>   - Geçerli WorldEdit seçimini verilen isimle kaydeder.
/area remove <isim> - Belirtilen bölgeyi siler.
/area info <isim>   - Bölge koordinatlarını ve içindeki oyuncu sayısını gösterir.
/area list          - Kayıtlı tüm bölgeleri listeler.
/area list <isim>   - Bölge için kullanabileceğiniz placeholder bilgisini gösterir.
/area reload        - Yapılandırmayı yeniden yükler.
```

### Yapılandırma

`config.yml` dosyasıyla alt komut adlarını değiştirebilir, mesajların İngilizce veya Türkçe çevirilerini özelleştirebilirsiniz. Ana komut ismi `/area` olarak sabittir ve yapılandırma ile değiştirilemez.

### Derleme (Jar Oluşturma)

Projenin derlenebilmesi için Java 21 ve Gradle'ın yüklü olması gerekir. Aşağıdaki komutu çalıştırın:

```bash
gradle build
```

Derleme tamamlandığında `build/libs/AreaPlayerControl-1.0-SNAPSHOT.jar` dosyası oluşur. Bu jar dosyasını PaperMC sunucunuzun `plugins/` klasörüne kopyalayarak eklentiyi kullanmaya başlayabilirsiniz.

WorldEdit eklentisinin sunucuda yüklü olması gerektiğini unutmayın.

Eklenti, PlaceholderAPI yüklü ise her kayıtlı bölge için `%areaplayercontrol_players_<isim>%` biçiminde bir placeholder sağlar. Bu placeholder ilgili bölge içindeki çevrimiçi oyuncu sayısını döndürür ve TitleManager gibi eklentiler tarafından skorboardlarda kullanılabilir. Placeholder bilgisini hızlıca öğrenmek için `/area list <isim>` komutunu kullanabilirsiniz.
