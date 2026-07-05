<?php



use Illuminate\Support\Facades\Route;
use Illuminate\Support\Facades\Artisan;
use App\Http\Controllers\UserController;

use App\Http\Controllers\SettingController;
use App\Http\Controllers\ContactsController;
use App\Http\Controllers\DashboardController;
use App\Http\Controllers\UserProfileController;


use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Storage;


use Illuminate\Support\Facades\Response;


/*
|--------------------------------------------------------------------------
| Web Routes
|--------------------------------------------------------------------------
|
| Here is where you can register web routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "web" middleware group. Make something great!
|
*/


Route::get('link', function () {
    try {
        Artisan::call('storage:link');
        $output = Artisan::output();

        // Return a success message along with Artisan output
        return response()->json(['success' => true, 'message' => 'Storage link created successfully.', 'artisan_output' => $output]);
    } catch (Exception $e) {
        // Return an error message if something goes wrong
        return response()->json(['success' => false, 'message' => 'Failed to create storage link.', 'error' => $e->getMessage()]);
    }
});

Route::get('clear', function () {
    try {
        Artisan::call('optimize:clear');
        $output2 = Artisan::output();

        // Return a success message along with Artisan output
        return response()->json(['success' => true, 'message' => 'optimize successfully.', 'artisan_output' => $output2]);
    } catch (Exception $e) {
        // Return an error message if something goes wrong
        return response()->json(['success' => false, 'message' => 'Failed to optimize.', 'error' => $e->getMessage()]);
    }
});




Route::get('/', [UserController::class, 'index'])->name('index');
Route::get('login', [UserController::class, 'login'])->name('login');
Route::post('login', [UserController::class, 'loginPost'])->name('login_post');
Route::get('api', [SettingController::class, 'api']);
Route::post('ads', [SettingController::class, 'ads'])->name('ads');
Route::get('contacts/search', [ContactsController::class, 'search'])->name('contacts.search');

Route::get('contacts/spam', [ContactsController::class, 'spamContacts'])->name('contacts.spam');

Route::post('import-contacts', [ContactsController::class, 'import'])->name('contacts.import');
Route::get('contacts/create/bulk', [ContactsController::class, 'bulk'])->name('contacts.bulk');


Route::get('contacts/example', function () {
    $filePath = public_path('example_contacts.xlsx');

    return Response::download($filePath, 'example_contacts.xlsx');
})->name('contacts.example');


Route::get('contacts/export-contacts-sql', [ContactsController::class, 'exportSql'])->name('export.contacts.sql');
Route::post('contacts/import-contacts-sql', [ContactsController::class, 'importSql'])->name('import.contacts.sql');


Route::middleware(['auth'])->group(function () {
    Route::get('logout', [UserController::class, 'logout'])->name('logout');
    Route::get('profile', [UserController::class, 'profile'])->name('profile');
    Route::post('profile', [UserController::class, 'updateProfile'])->name('update.profile');
    Route::resource('users', UserProfileController::class);

    Route::resource('contacts', ContactsController::class);

    Route::get('dashboard', [DashboardController::class, 'index'])->name('dashboard');

    Route::get('notification', [SettingController::class, 'notification_index'])->name('notification.index');
    Route::post('notification_resend', [SettingController::class, 'notification_Resend'])->name('notification.resend');
    Route::delete('notification_delete/{id}', [SettingController::class, 'notification_delete'])->name('notification.delete');
    Route::post('notification', [SettingController::class, 'notification_Post'])->name('notification.post');
    Route::post('notification/send', [SettingController::class, 'notification_Send'])->name('notification.send');
    Route::get('app_update', [SettingController::class, 'appUpdate'])->name('app.update');
    Route::post('app_update', [SettingController::class, 'updatePost'])->name('update.post');
    Route::get('settings', [SettingController::class, 'settings'])->name('settings');
    Route::post('settings/about', [SettingController::class, 'about'])->name('settings.about');
    Route::post('settings/privacy_policy', [SettingController::class, 'privacyPolicy'])->name('settings.privacy');
    Route::post('settings', [SettingController::class, 'settingsPost'])->name('settings.post');



});




