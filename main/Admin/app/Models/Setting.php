<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Setting extends Model
{
    use HasFactory;

    protected $fillable = [
        'onesignal_app_id',
        'onesignal_rest_key',
        'app_new_version',
        'app_update_status',
        'app_redirect_url',
        'app_update_desc',
        'app_description',
        'app_email',
        'app_author',
        'app_contact',
        'app_website',
        'app_developed_by',
        'privacy_policy',
        'more_apps_url',
        'isMaintenance',



    ];
}
