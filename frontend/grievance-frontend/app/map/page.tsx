'use client';
import dynamic from 'next/dynamic';
import { MessageSquareWarning } from 'lucide-react';
import Link from 'next/link';
import { Button } from '@/components/ui/button';

const MapView = dynamic(() => import('@/components/Mapview'), { ssr: false });

export default function MapPage() {
  return (
    <div className="min-h-screen flex flex-col">
      <header className="bg-white border-b border-neutral-200 px-8 py-4 flex justify-between items-center">
        <div className="flex items-center gap-2">
          <div className="size-8 rounded-lg bg-neutral-900 text-white flex justify-center items-center">
            <MessageSquareWarning className="size-4" />
          </div>
          <span className="font-bold">GrievanceOS — Live Map</span>
        </div>
        <div className="flex gap-2">
          <Link href="/dashboard">
            <Button variant="outline" size="sm">Dashboard</Button>
          </Link>
          <Link href="/complaints/new">
            <Button size="sm" className="bg-neutral-900 text-white">+ File Complaint</Button>
          </Link>
        </div>
      </header>
      <div className="flex-1">
        <MapView />
      </div>
    </div>
  );
}