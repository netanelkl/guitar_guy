using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ChordFingerFinder
{
    class Program
    {
        static CNoteFingerPosition GetNextPositionForNote(int note)
        {
            return GetNextPositionForNote(note, null);
        }

        static CNoteFingerPosition GetNextPositionForNote(int eNote, CFingerPosition sLastPosition)
        {
            CNoteFingerPosition sCurrentNote = null;

            if (sLastPosition != null)
            {
                sCurrentNote = new CNoteFingerPosition(sLastPosition, eNote);
                sCurrentNote.AdvanceFrets(1);
            }
            else
            {
                sCurrentNote = new CNoteFingerPosition(Globals.s_posFirstGuitarNote);
            }

            // Go through the notes to find that requested note.
            bool fNoteFound = false;

            // Kidder asks: You just set it to false, why check?
            while (!fNoteFound)
            {
                // Check if we got our note.
                if (eNote == sCurrentNote.Note)
                {
                    fNoteFound = true;
                    break;
                }

                if (!sCurrentNote.AdvanceFrets(1))
                {
                    throw new Exception("What the fuck you want?");
                }
            }

            return sCurrentNote;
        }

        static public CGuitarHand GetFingerPosition(int[] lstNotes)
        {
            CGuitarHand rResult = null;

            // Get the first position for the starting note.
            CNoteFingerPosition posStart = null;

            while (true)
            {
                rResult = new CGuitarHand();

                if (posStart == null)
                {
                    posStart =
                        new CNoteFingerPosition(GetNextPositionForNote(lstNotes[0]));
                }
                else
                {
                    // Get the first position for the starting note
                    posStart =
                        new CNoteFingerPosition(GetNextPositionForNote(posStart.Note, posStart.FingerPosition));
                }

                if (!rResult.Add2(posStart))
                {
                    continue;
                }

                CNoteFingerPosition currPos;

                bool fContinue = false;

                // Now get all the rest.
                for (int nNoteIndex = 1; nNoteIndex < lstNotes.Length; nNoteIndex++)
                {
                    // Get the next position.W
                    currPos =
                        new CNoteFingerPosition(posStart);

                    if (!currPos.AdvanceFrets(lstNotes[nNoteIndex] - lstNotes[0]))
                    {
                        break;
                        //throw new Exception("I realy dont care anymore");
                    }

                    if (!rResult.Add2(currPos))
                    {
                        fContinue = true;
                        break;
                    }
                }

                if (fContinue)
                {
                    continue;
                }

                break;
            }

            if (!rResult.GenerateFingerPositioningFromLists())
            {
                return null;
            }

            return rResult;
        }

        static void Main(string[] args)
        {
            //int[] lstNotes = {0, 4, 7, 12, 16, 19, 24, 28 };
            int[] lstNotes = { 0, 3, 7 };
            int[] multiplied = new int[12];
            for (int multiply = 0; multiply < 4; multiply++)
            {
                for (int i = 0; i < lstNotes.Length; i++)
                {
                    multiplied[multiply * lstNotes.Length + i] = lstNotes[i] + 12 * multiply + (int)ENotes.E;
                }
            }
            CGuitarHand cHand = GetFingerPosition(multiplied);

            Console.WriteLine("cHand: " + cHand.ToString());
            Console.ReadLine();
        }
    };
}
