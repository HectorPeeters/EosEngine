# GameEngine

A fully custom game engine written in Java.

## Things To Do

- [x] Add system functionality
- [x] Add update timer
- [x] Add basic opengl
- [x] Add process manager
- [x] Add decent logging system
- [x] Gson for scene loading
- [ ] Add audio support
- [ ] Optimize EventSystem
- [ ] Add physics
- [ ] Add file watch
- [ ] Then add auto reload
- [ ] FIX RESOURCE SYSTEM
- [ ] Add better serialization and file IO system
- [ ] Resource dependency
- [ ] Add better config system

## Resources

There are different kind of asset files: textures, animations, audio effects, shaders, scripts, ...
To load an asset you would probably use

```java
ResourceSystem.<AssetType>getResource("path").
```

By using this technique the engine stays very modular so we can easily add new asset types if needed.

### Resource Types

- Animations: depends on a texture and an animation file
- Textures:   only image data
- Shaders:    only text
- Scripts:    only text