<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up()
    {
        Schema::create('contacts', function (Blueprint $table) {
            $table->id();
            $table->string('image')->nullable();
            $table->string('name');
            $table->string('phoneNumber');
            $table->boolean('isSpam')->default(false);
            $table->string('spamType')->nullable();
            $table->string('tag')->nullable();
            $table->string('carrierName')->nullable();
            $table->string('countryName')->nullable();
            $table->string('appuser_id')->nullable();
            $table->timestamps();

        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('contacts');
    }





};
