import type { NextPage } from 'next';
import { ClientForm } from '../../../src/modules/clients/components/ClientForm';

const ClientEditPage: NextPage = () => {
  return (
    <div className="p-6">
      <ClientForm isEditMode />
    </div>
  );
};

export default ClientEditPage;