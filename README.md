## Overview

RankManager is a Spring Boot–based REST API for managing player ranks and rank assignments, backed by MongoDB and secured with an API key and optional rate limiting.

The core concepts are:

- **Ranks**
  - Identified by a stable `rankId` (e.g. `Admin`, `Mod`).
  - Have a human-readable `displayName`, an integer `priority`, and sets of:
    - `ownPermissions` – permissions defined directly on the rank.
    - `effectivePermissions` – resolved permissions including inherited ranks.
  - Can **inherit** from other ranks via `inheritedRankIds`, enabling permission trees.

- **Player Rank Assignments**
  - Link a `playerId` to a `rankId`.
  - Track who assigned the rank (`assignedById`) and when (`assignedAtTimestamp`).
  - Are stored separately from the rank definitions so ranks can be updated without touching assignments.

The API exposes JSON endpoints under `/secure/api/**` to:

- Create, read, update, and delete ranks.
- Add or remove permissions and inheritance on a rank.
- Assign ranks to players and remove those assignments.
- Query:
  - All ranks.
  - A single rank by ID or by display name.
  - All ranks assigned to a player.
  - All players that have a given rank.

Persistence is handled via Spring Data MongoDB repositories (`RankObject` and `PlayerRankAssignmentObject` documents), with custom repository implementations (`CustomRankRepositoryImpl` and `CustomRankAssignmentRepositoryImpl`) for complex updates like recomputing effective permissions and cascading inheritance changes.

The service layer (`RankService`) contains the business rules and validations (e.g., valid rank names/priorities, uniqueness checks, and error handling via domain exceptions). A small mapping layer converts Mongo documents into API-facing DTOs.

Security is provided by Spring Security with a custom `SimpleTokenAuthFilter` that enforces an API key on `/secure/api/**`, and a `RateLimitFilter` based on Bucket4j that can throttle requests per API key or client. Together, they make RankManager suitable as a backend service for games or applications needing centralized, configurable rank and permission management.
