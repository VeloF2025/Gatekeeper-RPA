import { GetServerSideProps } from 'next';
import { getAuth } from '@clerk/nextjs/server';
import { useRouter } from 'next/router';
import dynamic from 'next/dynamic';

const ProjectDetail = dynamic(() => import('@/modules/projects/components/ProjectDetail').then(mod => mod.ProjectDetail || mod.default), {
  ssr: false,
  loading: () => <div>Loading project...</div>
});

export default function ProjectDetailPage() {
  const router = useRouter();
  const { id } = router.query;
  
  if (!id) return <div>Loading...</div>;
  
  return <ProjectDetail projectId={id as string} />;
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