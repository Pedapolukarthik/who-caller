<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Ads extends Model
{
    use HasFactory;
    protected $fillable = [
        'ad_status',
        'main_ads',
        'admob_publisher_id',
        'admob_banner_unit_id',
        'admob_interstitial_unit_id',
        'admob_native_unit_id',
        'admob_app_open_unit_id',
        'unity_game_id',
        'unity_banner_placement_id',
        'unity_interstitial_placement_id',

    ];
}
