# AttemptsStatistic

The attempt counter for the hardcore minecraft server, outputs the attempt number to all players when players respawn

## Configuration

| Setup | Default | Description |
|--------------|---------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `attempts`     | `int` | Number of attempts |
| `show-title-on-join` | `true` | should the attempt number be shown when the player joins       |
| `title-main` | `string` | the main title that appears on a new attempt |
| `title-sub` | `string` | the sub title that appears on a new attempt |
| `join-title-main` | `string` | the main title that appears on a player joins |
| `join-title-sub` | `string` | the sub title that appears on a player joins |

## Commands

| Command         | Permission     | Description               |
|-----------------|----------------|---------------------------|
| `/setattempts <reset\|set <number>>` | `attemptsstatictic.admin` | Admin commands for attempts counter |
| `/attempts` | `attemptsstatictic.mystats` | Show attempt statistics |
