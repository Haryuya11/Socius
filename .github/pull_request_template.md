## Description
Please include a summary of the change and which issue is fixed. Also include relevant motivation and context.

## Type of Change
- [ ] **Bug fix** (non-breaking change which fixes an issue)
- [ ] **New feature** (non-breaking change which adds functionality)
- [ ] **Breaking change** (fix or feature that would cause existing functionality to not work as expected)
- [ ] **Refactoring** (code restructuring without changing logic)
- [ ] **Chore / Configuration** (updating `libs.versions.toml`, Gradle, CI/CD, etc.)

## Checklist
### Code Understanding & Quality
- [x] I fully understand the logic I implemented and its impact on the system.
- [x] I have performed a self-review of my code.
- [x] **Clean Commit:** I have verified my commits are clean (no hardcoded secrets/tokens, no commented-out code, no `System.out.println`).
- [x] My code follows the project's coding conventions (ran `spotlessCheck` / `checkstyle`).

### Database & SQL
- [ ] This PR introduces database changes.
- [ ] **SQL Check:** If yes, I have successfully executed the SQL scripts on my local database without errors.
- [ ] I have verified that there are no "N+1 query" performance issues.

### Testing & Verification
- [ ] **Localhost:** I have run the application on `localhost` and verified the feature/fix works as expected.
- [ ] I have added/updated Unit Tests (JUnit 5) for the new logic.
- [ ] I have build successfully (`./gradlew clean build`).
- [ ] All local tests passed (`./gradlew test`).

###  Dependencies
- [x] If adding a new library, I defined it in `libs.versions.toml` (Version Catalog).