import { GetServerSideProps } from 'next';
import { getAuth } from '@clerk/nextjs/server';
import { useRouter } from 'next/router';
import dynamic from 'next/dynamic';

const ProjectForm = dynamic(() => import('@/modules/projects/components/ProjectForm').then(mod => mod.ProjectForm || mod.default), {
  ssr: false,
  loading: () => <div>Loading...</div>
});

export default function EditProjectPage() {
  const router = useRouter();
  const { id } = router.query;
  
  if (!id) return <div>Loading...</div>;
  
  return <ProjectForm projectId={id as string} mode="edit" />;
}

export const getServerSideProps: GetServerSideProps = async (ctx) => {
  const { userId } = getAuth(ctx.req);

  if (!userId) {
    return {
      redirect: {
        destination: '/sign-in',
        permanent: false,
      },
    };
  }

  return { props: {} };
};