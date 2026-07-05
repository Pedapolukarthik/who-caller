<?php

namespace App\Imports;

use App\Models\Contacts;
use Maatwebsite\Excel\Concerns\ToModel;

use Maatwebsite\Excel\Concerns\WithHeadingRow;


class ContactsImport implements ToModel, WithHeadingRow
{
    /**
     * @param array $row
     *
     * @return \Illuminate\Database\Eloquent\Model|null
     */
    public function model(array $row)
    {

        // Check if contact already exists by name and phone number
        $existingContact = Contacts::where('name', $row['name'])
            ->where('phoneNumber', $row['number'])
            ->first();

        // If contact already exists, return null (don't insert)
        if ($existingContact) {
            return null;
        }

        // Otherwise, insert new contact
        return new Contacts([
            'name'   => $row['name'],
            'phoneNumber' => $row['number'],
        ]);
    }
}
