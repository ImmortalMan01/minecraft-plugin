# AreaPlayerControl

AreaPlayerControl, PaperMC tabanlı Minecraft sunucuları için basit bir alan yönetim eklentisidir. WorldEdit eklentisinden yararlanarak seçtiğiniz bölgeleri kaydetmenizi, silmenizi ve listelemenizi sağlar. Ayrıca kayıtlı bölgeler içindeki oyuncu sayısını görüntülemeye yarayan bir `info` komutuna sahiptir.

## Komutlar

```
/area save <isim>   - Geçerli WorldEdit seçimini verilen isimle kaydeder.
/area remove <isim> - Belirtilen bölgeyi siler.
/area info <isim>   - Bölge koordinatlarını ve içindeki oyuncu sayısını gösterir.
/area list          - Kayıtlı tüm bölgeleri listeler.
```

## Yapılandırma

`config.yml` dosyasıyla ana komut adını ve alt komutları değiştirebilir,
mesajların Türkçe çevirilerini de özelleştirebilirsiniz. `commands.base`
anahtarını düzenleyerek `/area` komutunun adını değiştirebilirsiniz.

## Derleme (Jar Oluşturma)

Projenin derlenebilmesi için Java 21 ve Gradle'ın yüklü olması gerekir. Şu adımları izleyerek eklenti jar dosyasını oluşturabilirsiniz:

1. Depo kök dizininde aşağıdaki komutu çalıştırın:

   ```bash
   gradle build
   ```

2. Derleme tamamlandığında `build/libs/AreaPlayerControl-1.0-SNAPSHOT.jar` dosyası oluşur. Bu jar dosyasını PaperMC sunucunuzun `plugins/` klasörüne kopyalayarak eklentiyi kullanmaya başlayabilirsiniz.

WorldEdit eklentisinin sunucuda yüklü olması gerektigini unutmayın.
