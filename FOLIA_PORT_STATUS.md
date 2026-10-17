# Folia 1.21.11 port status

Status: partial port; Folia support is **not declared**. The plugin has not been run on Folia 1.21.11, and GitHub Actions has not yet produced a result for this branch.

## Changes made

- Updated the compile-only Paper API target to 1.21.11.
- Added `TaskScheduler`, which detects Folia's global region scheduler and routes global repeating tasks, entity repeating tasks, and entity delayed tasks to Folia schedulers. Paper uses Bukkit scheduler fallbacks.
- Changed pet display ticking to schedule each player's pet work on that player's entity scheduler. Its UUID-indexed tracking and smoothing maps are concurrent because the global ticker and player-owned work now run on different threads.
- Routed bleed damage ticks to the damaged entity's scheduler and GUI follow-up opens to the relevant player's scheduler.
- Routed periodic auto-deactivation checks to each player scheduler.
- Added a small scheduler-detection unit test and retained GitHub Actions as the only build/test runner for this port.

## Safe by inspection, not runtime verified

- The changed scheduled pet entity mutations (spawn, remove, name/rotation updates and teleport) execute within the player's or pet entity's scheduler callback.
- Bleed damage and periodic player state checks execute in entity-owned callbacks.
- GUI callbacks capture a player and execute in that player's scheduler context.
- Storage formats, config keys, resource files, and `LICENSE.md` were not changed.

These statements describe the edited paths only; they do not establish whole-plugin Folia safety.

## Still unverified / audit required

- Other plugin code reads and mutates Bukkit entities, players, inventories, worlds, shared collections, and configuration from command, event, integration, and API entry points. Those paths have not all been proven to execute in the correct region context.
- Console `dispatchCommand` calls occur in prestige, breakthrough, and configured effects. Their correct Folia context and downstream command compatibility have not been validated.
- Custom event dispatch is synchronous. Safety depends on the calling context and third-party listeners; listener compatibility is unknown.
- Mount operations and pet-display teleports can move entities. Cross-region teleport behavior, vehicle ownership, and concurrent player/entity movement have not been tested on Folia.
- `Bukkit.getOnlinePlayers()` is enumerated by the global repeating task, which then queues player-owned work. Runtime behavior and load under high player counts need Folia testing.
- The plugin has other shared mutable state, including pet/player registries and combat maps. The scheduler changes do not make those structures generally thread-safe.
- Async storage and integration callbacks need a separate full audit to ensure Bukkit objects are not accessed from asynchronous completions.
- Paper compatibility against the declared 1.21.11 API has not been built or runtime tested. The bridge's Paper fallback is present, but compatibility with earlier Paper versions is not guaranteed by this port.

## Verification gate

Run the existing GitHub Actions Build workflow on `folia/1.21.11`. It runs `./gradlew build`, which includes the new unit test. Do not treat a passing build as Folia runtime validation. Before declaring support, complete the audit above and exercise pet lifecycle, combat effects, menus, mount/dismount, reload, configured rewards/effects, world changes, disconnects, and shutdown on both Folia 1.21.11 and Paper 1.21.11.
