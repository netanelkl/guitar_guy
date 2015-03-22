package com.mad.tunerlib.app;

import android.content.Context;

import com.mad.lib.R;
import com.mad.lib.app.IServiceProviderRegistrar;
import com.mad.lib.utils.AppLibraryServiceProvider;
import com.mad.lib.utils.bitmaps.BitmapsStore;
import com.mad.tunerlib.activities.TunerActivity;
import com.mad.tunerlib.chordGeneration.ChordGenerator;
import com.mad.tunerlib.chordGeneration.GuitarPositionManager;
import com.mad.tunerlib.recognition.AudioIn;
import com.mad.tunerlib.recognition.ChordRecognizer;
import com.mad.tunerlib.recognition.INativeInterfaceWrapper;
import com.mad.tunerlib.recognition.NoteRecognizer;
import com.mad.tunerlib.recognition.RealNativeInterfaceWrapper;
import com.mad.tunerlib.recognition.TestingNativeInterfaceWrapper;
import com.mad.tunerlib.utils.GuitarNotePlayer;
import com.mad.tunerlib.utils.Bitmaps.TunerActivityBitmapStore;

public class TunerServiceProviderRegistrar implements
		IServiceProviderRegistrar
{

	@Override
	public AppLibraryServiceProvider build(	AppLibraryServiceProvider provider,
											Context context)
	{
		AudioIn m_AudioCapturer = new AudioIn(context);
		m_AudioCapturer.start();

		INativeInterfaceWrapper interfaceWrapper;
		String arch = System.getProperty("os.arch");
		if (com.mad.lib.utils.Definitions.DebugFlags.FAKE_AUDIO_PROCESSING_MODE
				|| arch.equals("i686"))
		{
			interfaceWrapper =
					new TestingNativeInterfaceWrapper();
		}
		else
		{
			interfaceWrapper = new RealNativeInterfaceWrapper();
		}
		provider.register(R.service.guitar_note_player,
				new GuitarNotePlayer(context));
		provider.register(R.service.native_interface_wrapper,
				interfaceWrapper);
		provider.register(R.service.chord_generator,
				new ChordGenerator());
		provider.register(R.service.position_manager,
				new GuitarPositionManager());
		provider.register(R.service.note_recognizer,
				new NoteRecognizer(m_AudioCapturer));
		provider.register(R.service.chord_recognizer,
				new ChordRecognizer(m_AudioCapturer));
		BitmapsStore.register(TunerActivity.class,
				new TunerActivityBitmapStore());
		return provider;
	}
}
