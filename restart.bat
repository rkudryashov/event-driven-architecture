docker compose down --volumes
rmdir /s /q misc\kafka_data
@REM build all Docker images in parallel
call gradlew :book-service:bootBuildImage :user-service:bootBuildImage :notification-service:bootBuildImage || exit /b
@REM build all Docker images sequentially
@REM call gradlew :book-service:bootBuildImage || exit /b
@REM call gradlew :user-service:bootBuildImage || exit /b
@REM call gradlew :notification-service:bootBuildImage || exit /b
docker compose up
