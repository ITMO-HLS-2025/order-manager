#!/usr/bin/env bash
set -e

OLD_NAME="order-service"
NEW_NAME="order-manager"

GITHUB_OWNER="ITMO-HLS-2025"
OLD_REPO="order-service"
NEW_REPO="theatre-manager"

echo "🔁 Rename project: $OLD_NAME → $NEW_NAME"

# --- 1. Переименование директории ---
cd ..
mv "$OLD_NAME" "$NEW_NAME"
cd "$NEW_NAME"

# --- 2. Maven ---
if [ -f pom.xml ]; then
  echo "📦 Updating pom.xml"
  sed -i "s/$OLD_NAME/$NEW_NAME/g" pom.xml
fi

# --- 3. Gradle ---
[ -f settings.gradle ] && sed -i "s/$OLD_NAME/$NEW_NAME/g" settings.gradle
[ -f build.gradle ] && sed -i "s/$OLD_NAME/$NEW_NAME/g" build.gradle

# --- 4. Java packages (snake_case / kebab-case если есть) ---
echo "☕ Updating Java sources"
find src -type f -name "*.java" \
  -exec sed -i \
  -e "s/order_service/order_manager/g" \
  -e "s/order-service/order-manager/g" {} \;

# --- 5. Удаление GitHub репозитория ---
echo "🗑 Deleting GitHub repo $GITHUB_OWNER/$OLD_REPO"

curl -s -X DELETE \
  -H "Authorization: token $GITHUB_TOKEN" \
  -H "Accept: application/vnd.github+json" \
  "https://api.github.com/repos/$GITHUB_OWNER/$OLD_REPO"

echo "✅ Old repo deleted"

# --- 6. Удаляем remote ---
git remote remove origin || true

# --- 7. Коммит ---
git add .
git commit -m "Rename project: order-service → theatre-manager"

echo "🎉 Done"
echo "👉 Создай новый репозиторий и выполни:"
echo "git remote add origin https://github.com/$GITHUB_OWNER/$NEW_REPO.git"
echo "git push -u origin main"
