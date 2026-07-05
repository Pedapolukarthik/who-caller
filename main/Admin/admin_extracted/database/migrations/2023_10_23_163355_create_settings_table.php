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
        Schema::create('settings', function (Blueprint $table) {
            $table->id();
            $table->bigInteger('app_status')->nullable();

            $table->string('onesignal_app_id')->nullable();
            $table->string('onesignal_rest_key')->nullable();
            $table->string('more_apps_url')->nullable();
            $table->text('privacy_policy')->nullable();
            $table->bigInteger('isMaintenance')->nullable();
            $table->bigInteger('app_update_status')->nullable();

            $table->string('app_new_version')->nullable();
            $table->string('app_update_desc')->nullable();
            $table->string('app_redirect_url')->nullable();

            $table->string('app_email')->nullable();
            $table->string('app_author')->nullable();
            $table->string('app_contact')->nullable();
            $table->string('app_website')->nullable();
            $table->string('app_developed_by')->nullable();
            $table->string('app_description')->nullable();

            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('settings');
    }
};
