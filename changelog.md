
### Create compat:
- Added schematic support. If a proxy's target is within the schematic's bounding box, its position will be saved and loaded via a relative offset.
  - This means that you can safely save schematics of entire redstone systems without the proxy targets breaking.
  - Proxies targeting outside the bounding box will still save their targets in world space, which means you can safely copy stable "receiver" systems.

###  Sable compat:
- Proxies can now target into and out of sublevels. 
  - Proxy targets (loaded or unloaded) also properly move into the sublevels on assembly.
  - If you don't care about technical jargon, this essentially means that most things you can imagine should work now work.

### General.
- Made mixins less invasive by using modern MixinExtras features, improving compatibility with other mods.
- The previously mentioned Create support should also improve how easy it is to integrate proxies into structure worldgen.
- Redstone proxies have been upgraded. They now have two modes: Read and Write (You can switch between them by hitting the proxy with the tuner). Additionally, they can target a specific face instead of the whole block, allowing more precise control.
- If you select a block with the Tuner then Shift + Scroll, you can shift your selection towards/away from you. Useful when your contraptions are compact.
