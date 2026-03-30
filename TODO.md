# Current features:
- Piston Proxy
- Redstone Proxy
- Observer Proxy
- Vision Proxy
- Interaction Proxy
- Tuner to configure mod features
- Allay configurable searchOrigin, maxDistance, and general player tracking

## BUGS:
    Also selected position should never be accessed on server...
      Switch it to ClientPlayer to ensure.

    Observer proxy south texture is inverted for on and off
    I discovered that the synchronization is fragile in more complex proxy applications. Tested the 3x3 door.
        The desync actually happens naturally even without manual setTargetAbsPos()

### Debt:
- Have saving target to nbt by relative or by absolute be toggleable via a boolean field and the tuner. Should probably be relative by default to improve mod compat.
- Datagen (unavoidable for proxy colors...)
- Write common class code for serverside packet handling. Right now each overwrite needs to rewrite it.
- Redstone Proxy works via tick scheduling to avoid lag in edgecases.
- Test everything in multiplayer

# TODO FOR NEXT RELEASE:

    Redstone utility blocks:
      A proper redstone level display block.
        No read or write, and more verbose than the Redstone Proxy
        Does conduct redstone, useful RP target block
    
    Color coding for proxies?
    
    Piston proxy target field is modified before the base proxy setter to fix flickering.
      Client and server synching fixes:
        Ensure flickering is fixed
        Keep a registry on the server synched with the client to allow piston proxies outside render distance to visualize correctly
      
    Mix into vanilla code to fix light flickering on piston push.
      I have to add an extra blockstate property to MovingPistonBlock, 
      then make sure it is set to the light of the block being moved by the piston logic
      
    Proxy tuner upgrades:
      Selection clearing switch to left click
      
      Aid in the selection of multiple proxies and targets at once.
      Do things such as:
        Select all proxies targeting this block, then change them all to a new one.
        Offset my selection in a direction
      System for worldwide changes, not just the proxies loaded on the client

    Add a way to tell in-world when something is being targeted.
      A small lingering particle, maybe, or something customizable per-proxy.
    
    Way to disable block noise in an area. Server sound mixin and a registered proxy could work.
      In this mod, but also maybe make it a datapack

    Proxy Notebook:
      Lets you track positions in the world and select them.
    
    A big issue with the Piston Proxy weakly powered functionality is this:
      The redstone states need to change between push and pull.
      It would be nice to find a static solution
      Maybe via an auxiliary target?
      I don't want to make it push-retract dependent
      Possible solutions:
        The current piston proxy stays the same, but an advanced version is added as well.
        The advanced one either:
        Power input can be a bitmask.
          I'm worried about it being too complex...
        Directional and power coming in from different points does different things
          2 blockstate properties: FACING(Direction) and ROTATION([0, 3])
          
    Interaction and Vision proxy targeting air support. Maybe even targeting entities also
      Custom raycast.

    Create compat for ponders and better tooltips
      Part of CBT
      When creating a schematic, if the target of the proxy is in the schematic, save by relative target pos, else by absolute.

    Storage Proxy, redirects storage access.

    Piston Proxy pushing blocks larger distances at a time.

    CC:Tweaked compat:
      Who triggered an interactionproxy last?
  

# Planned features (Possibly made a different mod):
- Glasses that you can put on an allay. Bigger glasses = bigger radius. Essentially a fun way to set maxDistance.

- Ponder scenes

- Make it possible for allays to detect other entities apart from just players. 
  - Spawn eggs could be used to set it.

- Allays don't move and are silent if you give them a specific name.

- A way to make integrating the allay into structure stuff easier.

- An entity that relays all forces done to it to a different entity. 
  - Probably not an allay 
  - Has both a free form and a solid form. Maybe copper oxidization? 
  - Is very easy to make path to a certain spot, can fly if needed. Meant to aid in making leash contraptions

- A way to turn any block into a fallingBlockEntity. (Works with feature above to then have some control over the falling block)

- Buildstone dimension where piston proxies can still push stuff in the real dimension

### Dubious:
- A way to push past the piston push limit in certain scenarios 
  - Could be as simple as changing an integer value in moveBlocks. 
  - The idea would be to make this dynamic in-game. No clue how to balance this.
- A way to change the direction the piston proxy pushes with. 
  - Maybe this can replace the use of the single axis piston proxy? It remaps all movement to the placed axis? 
  - Maybe you can make the targeted block move multiple tiles at once?
- A way to speed up the whole piston push process, but especially the MBE animation


