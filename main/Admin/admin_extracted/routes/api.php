<?php

use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use App\Http\Controllers\ContactController;
use App\Http\Controllers\SettingController;
use App\Http\Controllers\ContactsController;
use App\Http\Controllers\UserProfileController;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider and all of them will
| be assigned to the "api" middleware group. Make something great!
|
*/

Route::middleware('auth:sanctum')->get('/user', function (Request $request) {
    return $request->user();
});




Route::post('get-name', [ContactsController::class, 'getName']);
Route::post('save-contacts', [ContactsController::class, 'saveContacts']);
Route::post('contacts', [ContactsController::class, 'getContacts']);
Route::post('contacts/search', [ContactsController::class, 'searchApi']);
Route::post('contact-data', [ContactsController::class, 'getContactformphoneNumber']);
Route::post('save-contact-one', [ContactsController::class, 'saveOneContact']);
Route::post('getSearchContact-data', [ContactsController::class, 'getSearchContact']);

Route::get('api', [SettingController::class, 'api']);

Route::post('register-phone', [UserProfileController::class, 'registerPhone']);
Route::post('register-email', [UserProfileController::class, 'registerEmail']);
Route::post('register-google', [UserProfileController::class, 'registerGoogle']);
Route::post('get-profile', [UserProfileController::class, 'getProfile']);
Route::post('update-profile', [UserProfileController::class, 'updateProfile']);
Route::post('upload', [UserProfileController::class,'uploadImage']);

