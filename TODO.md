# Current features:
- 1 axis piston proxy. (TODO find a special use for )
- 3 axis piston proxy
- Allay configurable searchOrigin, maxDistance, and general player tracking

## BUGS:
    I think recipes for CBT are broken in 1.20.1. idk why tho.
    I discovered that the synchronization is fragile in more complex proxy applications. Tested the 3x3 door.
        The desync actually happens naturally even without manual setTargetAbsPos()
### Debt:
- Test everything in multiplayer
- Switch FailableResult in parsePos and setLinkedRelPos to normal exception handling.
- Transfer the POWER_LEVEL blockstate property to each inheriting class.


# TODO FOR NEXT RELEASE:
    Redstone Proxy
        Write: Power it with redstone
        Read: Use comparator (if the target block has special comparator interactions, it should output that)
    Observer Proxy
        Read only
    New particle system
        Particles properly track the selected proxy and the proxy's output/s.
        

# Planned features (Possibly made a different mod):
- Glasses that you can put on an allay. Could be something else. Suggestions open.
-     Bigger glasses = bigger radius. Essentially a fun way to set maxDistance.
-     Allay general entity detection

- Ponder optional dependency.

- Make it possible for allays to detect other entities apart from just players. 
  - Spawn eggs could be used to set it.

- Allays don't move and are silent if you give them a specific name.
- A way to make integrating the allay into structure stuff easier.

- An entity that relays all forces done to it to a different entity. 
  - Probably not an allay 
  - Has both a free form and a solid form. Maybe copper oxidization? 
  - Is very easy to make path to a certain spot, can fly if needed. Meant to aid in making leash contraptions

- A way to turn any block into a fallingBlockEntity. (Works with feature above to then have some control over the falling block)

- Buildstone dimension where proxies can work into the real dimension.

### Dubious:
- A way to push past the piston push limit in certain scenarios 
  - Could be as simple as changing an integer value in moveBlocks. 
  - The idea would be to make this dynamic in-game. No clue how to balance this.
- Additional functionality for Guided Proxy:
  - Can remap all movement to the selected axis.
  - Can change the selected axis on runtime.
  - Can sort of multiply the piston push.
- A way to speed up the whole piston push process, but especially the MBE animation


