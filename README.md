## Getting Started
- Setup environment var. in docker-compose.yml
- Create vehicleCapacities.json and set it as `volumes` in docker-compose.yml config
----------------------------------------------------------
### examples:
security:
user: "user"
password: "password"
# If would like to use source of data from file not from REST api
  path-to-json-counts-file: "./outer.json"
  interval-to-upload-json-file: "30"
#For dev only
name-mapping: "5:8"
----------------------------------------------------------
## Build 
```shell
./gradlew assemble
```

## Run
Requirement:
- `docker` - installed.

Command:
```shell
docker compose up -d
```
## Check
http://localhost:8081/v1/busloads

## Architecture

Spring Boot 3.x REST API that collects passenger counts from Pi-based sniffers (or files) and exposes per-vehicle occupancy as a percentage.

**Data flow:**
1. Raspberry Pi devices POST newline-delimited JSON logs to `POST /v1/upload-json` (Basic Auth required)
2. `CounterController` streams the request body into `CounterService.asyncParseJsonFile()` (runs on a separate thread via `@Async`)
3. The service parses each line as `LogEntryDto` (`__REALTIME_TIMESTAMP` + `MESSAGE`), keeps only the newest entry by timestamp
4. `BusLoadDto` is constructed from the log entry — it regex-parses `MESSAGE` for vehicle name and count, then calculates `currentFullness` (%) using capacity from `PeopleCountRepository.CAPACITY_CONFIGS`
5. Results are stored in `PeopleCountRepository` (singleton `ConcurrentHashMap`, keyed by vehicle name)
6. `GET /v1/busloads` returns the map, injecting a `"time"` sentinel entry and evicting records older than 30 minutes or with `currentFullness > 150`

**Optional file-based mode:** When `api.path-to-json-counts-file` is set, `CountsFromFileScheduler` polls that file every N seconds (default 15) and feeds data via `ScheduleTasksService`. Both the scheduler and its service are `@ConditionalOnProperty` gated on that property.

**Capacity config:** `vehicleCapacities.json` maps vehicle names to capacity divisors. In Docker it must be mounted at `/app/resources/vehicleCapacities.json`. `CapacitiesConfig` reads it directly from the classpath resources, so the file must exist before the app starts.

**Security:** `POST /v1/upload-json` requires HTTP Basic Auth (`api.security.user` / `api.security.password`). `GET /v1/busloads` is public.

**Dev-only name-mapping:** `api.name-mapping` in `application.yml` is a `oldName:newName` string that remaps one vehicle name at parse time — used for local testing when a device reports under a different ID.

## Key files

| File | Purpose |
|------|---------|
| `src/main/resources/application.yml` | All config; `api.name-mapping` is dev-only |
| `src/main/resources/vehicleCapacities.json` | Vehicle name → capacity (integer divisor) mapping |
| `docker-compose.yml` | Volume-mounts the JSON config; env vars for upstream APC/TC URLs |
| `src/test/resources/application-test.yml` | Test profile — stubs out upstream URLs |
