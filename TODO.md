# Current features:
- 1 axis piston proxy. (TODO find a special use for )
- 3 axis piston proxy
- Allay configurable searchOrigin, maxDistance, and general player tracking

## BUGS:
    Synchronization works like dogshit with weak powered.
    I discovered that the synchronization is fragile in more complex proxy applications. Tested the 3x3 door.
        The desync actually happens naturally even without manual setTargetAbsPos()
### Debt:
- Create generalized methods for setting a selection that also spawns a particle if on the client 
  - Possibly 2 methods: getter and a setter that both sets the necessary fields and spawn particles. 
- Test everything in multiplayer
- Switch FailableResult in parsePos and setLinkedRelPos to normal exception handling.
- Transfer the POWER_LEVEL blockstate property to each inheriting class.


# TODO FOR NEXT RELEASE:
    Port to 1.20.1

# Planned features (Possibly made a different mod):
### Future plans for particles:
    I want proxy particles to track their respective positions, but this requires rewriting a crap ton of the base mod code.
    Main issue is that proxy must be a BlockEntity in order to update a tracker that can then be used by the particle to move.
    However, all the logic is on the Block objects atm and moving it is nontrivial
- Glasses that you can put on an allay. Bigger glasses = bigger radius. Essentially a fun way to set maxDistance.

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

- Buildstone dimension where piston proxies can still push stuff in the real dimension

### Dubious:
- A way to push past the piston push limit in certain scenarios 
  - Could be as simple as changing an integer value in moveBlocks. 
  - The idea would be to make this dynamic in-game. No clue how to balance this.
- A way to change the direction the piston proxy pushes with. 
  - Maybe this can replace the use of the single axis piston proxy? It remaps all movement to the placed axis? 
  - Maybe you can make the targeted block move multiple tiles at once?
- A way to speed up the whole piston push process, but especially the MBE animation


