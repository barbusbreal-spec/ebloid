# EbloidStore — Android-клиент

Магазин приложений в стиле Google Play. Витрина каталога с иконками,
скриншотами, рейтингами и отзывами, аккаунты пользователей.

Бэкенд: [ebloidback](https://github.com/barbusbreal-spec/ebloidback) (Python/Flask).

## Стек
- Kotlin + Jetpack Compose (Material 3)
- Navigation Compose, ViewModel + StateFlow
- Retrofit + Moshi (REST API), OkHttp
- Coil (загрузка иконок/скриншотов)
- DataStore (хранение токена авторизации)

## Возможности
- Каталог опубликованных приложений, поиск и фильтр по категориям
- Карточка приложения: иконка, скриншоты, описание, версия, рейтинг
- Регистрация / вход по токену
- Отзывы с оценкой 1–5 звёзд (для авторизованных)
- Кнопка установки (скачивание APK с бэкенда)

## Настройка
Адрес бэкенда задаётся в `app/build.gradle.kts`:

```kotlin
buildConfigField("String", "API_BASE_URL", "\"https://ваш-домен.serv00.net/api/\"")
```

Для локального бэкенда на эмуляторе используйте `http://10.0.2.2:5000/api/`.

## Сборка
```bash
./gradlew assembleDebug
```
APK появится в `app/build/outputs/apk/debug/`.

> Для сборки нужен Android SDK (compileSdk 34). Откройте проект в Android Studio
> или укажите путь в `local.properties` (`sdk.dir=/path/to/Android/Sdk`).

## Структура
```
app/src/main/java/com/ebloid/store/
├── data/          модели, Retrofit API, сессия, репозиторий
├── ui/
│   ├── screens/   HomeScreen, AppDetailScreen, AuthScreen, ReviewDialog
│   ├── components/ AppRow, AppIcon, RatingStars
│   ├── theme/     тема Material 3
│   └── StoreViewModel.kt
├── EbloidApp.kt   ручной DI
└── MainActivity.kt навигация
```
