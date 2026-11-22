$functions = @("lose_heart", "refill_hearts", "earn_gems", "spend_gems", "update_daily_streak", "award_xp", "update_user_level", "complete_lesson", "complete_exercise_transactional", "check_achievements")

foreach ($fn in $functions) {
    Write-Host "Testing $fn..."
    $result = curl.exe -s -X POST -H "apikey: sb_publishable_1zTxwQY4Vx-jTRaFxKYJQg__Yf703nG" -H "Authorization: Bearer sb_publishable_1zTxwQY4Vx-jTRaFxKYJQg__Yf703nG" -H "Content-Type: application/json" "https://fpazflweyimrulwwvsdw.supabase.co/rest/v1/rpc/$fn" -d @C:\Users\YACINE\Desktop\sema\empty.json
    Write-Host $result
    Write-Host ""
}