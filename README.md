# TAMZ-Project

## Co je Tower Defence?
Tower Defence je hra v níž je účelem bránit svou základnu/pevnost. Toho docílí hráč budováním vlastních jednotek a obraných věží.

V tomto projektu budu implementovat hru tohoto druhu. Hra bude obsahovat mimo jiné menu, různé druhy map (načítané ze souboru), ukládání skóre a zvukové efekty. Hráč by měl mít možnot budovat několik druhů obranných věží. S jednotlivými levely by se měla zvyšovat i obtížnost.

## Finální podoba
Hra k vykreslování využívá canvas, součástí je vlastní menu, po dokončení levelu se uloží pomocí Shared Preferences informace o tom, kterým levelem hráč skončil a také jaké jsou nejlepší výsledky pro každé kolo, mapy jsou vloženy jako celistvé obrázky, ale cesta pro útočící lodě je vypočítávána ze souboru maps.txt, stejně tak jako počet a druhy lodí, které mají daný level vyrazit. Hra také obsahuje audio a zvukové efekty.
