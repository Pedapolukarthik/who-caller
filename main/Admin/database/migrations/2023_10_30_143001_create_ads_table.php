<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::create('ads', function (Blueprint $table) {
            $table->id();
            $table->timestamps();
            $table->bigInteger('ad_status')->nullable();
            $table->string('main_ads')->nullable();
            $table->string('admob_publisher_id')->nullable();
            $table->string('admob_banner_unit_id')->nullable();
            $table->string('admob_interstitial_unit_id')->nullable();
            $table->string('admob_native_unit_id')->nullable();
            $table->string('admob_app_open_unit_id')->nullable();
            $table->string('unity_game_id')->nullable();
            $table->string('unity_banner_placement_id')->nullable();
            $table->string('unity_interstitial_placement_id')->nullable();


        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('ads');
    }
};
